use std::collections::BTreeMap;
use itertools::Itertools;
use rust_aoc::solve_and_log;

const YEAR: u16 = 2023;
const DAY: u8 = 8;

fn main() {
    solve_and_log!(part1, part2);
}

fn route_len<P>(instr: &str, net: &BTreeMap<&str, (&str, &str)>, start: &str, end: P) -> usize
    where
        P: Fn(&str) -> bool
{
    let mut count = 0usize;
    let mut at = start;
    for x in instr.chars().cycle() {
        if end(at) {
            return count
        }
        at = match x {
            'L' => net[at].0,
            'R' => net[at].1,
            _ => return count
        };
        count += 1;
    }
    count
}

fn parse(input: &str) -> Option<(&str, BTreeMap<&str, (&str, &str)>)> {
    let (route, nodes) = input.split_once("\n\n")?;
    let net = nodes.split("\n").filter_map(|s| {
        if s.len() > 14 {
            Some((&s[0..=2], (&s[7..=9], &s[12..=14])))
        } else {
            None
        }
    }).collect::<BTreeMap<_, _>>();
    Some((route, net))
}

fn part1(input: &str) -> Option<u64> {
    let (route, net) = parse(input)?;

    let count = route_len(route, &net, "AAA", |at| at == "ZZZ");

    Some(count as u64)
}

fn part2(input: &str) -> Option<usize> {
    let (route, net) = parse(input)?;

    let starts = net.keys().filter(|&x| x.ends_with('A')).copied().collect_vec();
    let len = move |&start| route_len(route, &net, start, |x| x.ends_with('Z'));
    let counts = starts.iter().map(len).collect_vec();

    counts.into_iter().reduce(lcm)
}

fn gcd(mut n: usize, mut m: usize) -> usize {
    assert!(n != 0 && m != 0);
    while m != 0 {
        if m < n {
            std::mem::swap(&mut m, &mut n);
        }
        m %= n;
    }
    n
}

fn lcm(n: usize, m: usize) -> usize {
    n * m / gcd(n, m)
}

mod tests {
    use rust_aoc::{gen_test_main};
    gen_test_main!(part1 => 248422077);
    gen_test_main!(part2 => 249817836);
}
