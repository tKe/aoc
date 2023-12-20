use crate::{
    Condition::{GreaterThan, LessThan},
    Outcome::{Accept, Dispatch, Reject},
};
use itertools::Itertools;
use rust_aoc::{read_input, solve_and_log};
use std::collections::HashMap;
use std::num::ParseIntError;
use std::ops::RangeInclusive;

const YEAR: u16 = 2023;
const DAY: u8 = 19;

fn main() {
    let i = read_input(YEAR, DAY, "input").unwrap();
    let r = parse(i.as_str()).unwrap();
    // solve_and_log!(part1, part2 with example);
    solve_and_log!(part1, part2 with r);
}

#[derive(Debug)]
enum Outcome {
    Accept,
    Reject,
    Dispatch(u16),
}

impl TryFrom<&str> for Outcome {
    type Error = ParseIntError;

    fn try_from(value: &str) -> Result<Self, Self::Error> {
        match value {
            "R" => Ok(Reject),
            "A" => Ok(Accept),
            n => u16::from_str_radix(n, 36).map(|c| Dispatch(c)),
        }
    }
}

#[derive(Debug)]
enum Condition {
    GreaterThan(u16),
    LessThan(u16),
}

struct Workflow {
    conditions: Vec<(usize, Condition, Outcome)>,
    fixed: Outcome,
}

impl Workflow {
    fn outcome(&self, item: &Item) -> &Outcome {
        self.conditions.iter().find(|(f, c, _)| match c {
            GreaterThan(v) => item[*f] > *v,
            LessThan(v) => item[*f] < *v,
        }).map(|(_, _, o)| o).unwrap_or(&self.fixed)
    }

    fn parse_cond<S: Into<String>>(rule: S) -> Option<(usize, Condition, Outcome)> {
        let s = rule.into();
        let (cond, outcome) = s.split_once(':')?;
        let outcome = outcome.try_into().ok()?;
        let v = cond[2..].parse().ok()?;
        let [f, c] = cond.as_bytes()[..2] else { None? };
        let f = b"xmas".iter().position(|&it| it == f)?;
        let c = match c {
            b'>' => GreaterThan(v),
            b'<' => LessThan(v),
            _ => None?
        };
        Some((f, c, outcome))
    }
}

impl<A> FromIterator<A> for Workflow
    where A: Into<String> {
    fn from_iter<T: IntoIterator<Item=A>>(iter: T) -> Self {
        let mut conditions = vec![];
        let mut fixed = Reject;
        for s in iter {
            let s = s.into();
            let s = s.as_str();
            if let Some(c) = Self::parse_cond(s) {
                conditions.push(c);
            } else if let Ok(outcome) = s.try_into() {
                fixed = outcome;
                break;
            }
        }
        Workflow { conditions, fixed }
    }
}

struct Workflows {
    m: HashMap<u16, Workflow>,
}

impl Workflows {
    const IN: u16 = 671;
    // base36 of "in"
    pub fn accept(&self, item: &Item) -> bool {
        match self._accept(&Workflows::IN, item) {
            Accept => true,
            _ => false,
        }
    }

    fn _accept(&self, wf: &u16, item: &Item) -> Outcome {
        match self.m[&wf].outcome(item) {
            Dispatch(next) => self._accept(next, item),
            Accept => Accept,
            _ => Reject,
        }
    }

    fn accepts(&self) -> u64 {
        self._accepts(
            &Workflows::IN,
            [(1, 4000); 4],
        )
    }

    fn _accepts(&self, wf: &u16, ranges: [(u16, u16); 4]) -> u64 {
        let mut remaining = ranges;
        let mut count = 0u64;
        let w = &self.m[wf];
        for (f, c, o) in &w.conditions {
            let mut next = remaining.clone();
            next[*f] = Self::take_range(&mut remaining[*f], c);
            count += self.dispatch(next, o)
        }
        count + self.dispatch(remaining, &w.fixed)
    }

    fn dispatch(&self, remaining: [(u16, u16); 4], o: &Outcome) -> u64 {
        match o {
            Accept => remaining.iter().map(|(a,b)| if a <= b { 1 + b - a } else { 0 } as u64).product(),
            Dispatch(wf) => self._accepts(wf, remaining),
            _ => 0,
        }
    }

    fn take_range(r: &mut (u16, u16), c: &Condition) -> (u16, u16) {
        let (keep, take) = match *c {
            GreaterThan(v) => ((r.0, v), (v + 1, r.1)),
            LessThan(v) => ((v, r.1), (r.0, v - 1)),
        };
        *r = keep;
        take
    }
}

impl From<&str> for Workflows {
    fn from(value: &str) -> Self {
        let m = value
            .lines()
            .filter_map(|l| {
                let (key, rules) = l.split_once('{')?;
                let key = u16::from_str_radix(key, 36).ok()?;
                let rules = Workflow::from_iter(rules[..rules.len() - 1].split(','));
                Some((key, rules))
            })
            .collect();
        Workflows { m }
    }
}

type Item = [u16; 4];

fn parse(input: &str) -> Option<(Workflows, Vec<Item>)> {
    let (rules, items) = input.split_once("\n\n")?;
    Some((rules.into(), parse_items(items)))
}

fn parse_items(items: &str) -> Vec<Item> {
    items
        .split('\n')
        .filter_map(|l| {
            l.split([',', '=', '}'])
                .filter_map(|it| it.parse::<u16>().ok())
                .collect_vec()
                .try_into()
                .ok()
        })
        .collect_vec()
}

fn part1(r: &(Workflows, Vec<[u16; 4]>)) -> Option<u32> {
    let (rules, items) = r;
    Some(
        items
            .into_iter()
            .filter(|i| rules.accept(i))
            .fold(0, |c, [x, m, a, s]| c + (x + m + a + s) as u32),
    )
}

fn part2(r: &(Workflows, Vec<[u16; 4]>)) -> Option<u64> {
    let (rules, _) = r;
    Some(rules.accepts())
}

mod tests {
    use rust_aoc::gen_test_main;
    gen_test_main!(part1 => 425811);
    gen_test_main!(part2 => 131796824371749);
}
