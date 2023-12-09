use rust_aoc::solve_and_log;
use std::collections::HashSet;
use std::str::from_utf8;

const YEAR: u16 = 2017;
const DAY: u8 = 16;

fn main() {
    solve_and_log!(part1, part2);
}

fn part1(input: &str) -> Option<String> {
    Dance::parse(input).next()
}

fn part2(input: &str) -> Option<String> {
    let mut seen: HashSet<String> = HashSet::new();
    let lines: Vec<String> = Dance::parse(input)
        .take_while(|x| seen.insert(x.to_string()))
        .collect();
    lines
        .get(1_000_000_000 % lines.len() - 1)
        .map(|x| x.to_string())
}

struct Dance {
    dancers: Vec<char>,
    moves: Vec<DanceMove>,
}

impl Dance {
    fn parse(input: &str) -> Self {
        let dancers: Vec<char> = (b'a'..=b'p').map(char::from).collect();
        let moves = parse_moves(input);
        Dance { dancers, moves }
    }
}

impl Iterator for Dance {
    type Item = String;

    fn next(&mut self) -> Option<Self::Item> {
        self.moves.iter().for_each(|m| m(&mut *self.dancers));
        let s: String = self.dancers.iter().collect();
        Some(s)
    }
}

type DanceMove = Box<dyn Fn(&mut [char])>;

fn spin(n: usize) -> DanceMove {
    Box::new(move |dancers| dancers.rotate_right(n))
}

fn exchange(a: usize, b: usize) -> DanceMove {
    Box::new(move |dancers| dancers.swap(a, b))
}

fn pair(a: char, b: char) -> DanceMove {
    Box::new(move |dancers| {
        dancers
            .iter()
            .position(|&c| c == a)
            .zip(dancers.iter().position(|&c| c == b))
            .map(|(a, b)| dancers.swap(a, b))
            .unwrap()
    })
}

fn parse_moves(input: &str) -> Vec<DanceMove> {
    input
        .split(",")
        .map(|dance_move| match dance_move.as_bytes() {
            [b's', r @ ..] => {
                let c_str = from_utf8(r).expect("invalid count");
                spin(c_str.parse().unwrap())
            }
            [b'x', a1, b'/', b @ ..] => {
                let a = [*a1];
                let a_str = from_utf8(a.as_slice()).expect("failed to parse A");
                let b_str = from_utf8(b).expect("failed to parse B");
                exchange(a_str.parse().unwrap(), b_str.parse().unwrap())
            }
            [b'x', a1, a2, b'/', b @ ..] => {
                let a = [*a1, *a2];
                let a_str = from_utf8(a.as_slice()).expect("failed to parse A");
                let b_str = from_utf8(b).expect("failed to parse B");
                exchange(a_str.parse().unwrap(), b_str.parse().unwrap())
            }
            [b'p', a, b'/', b] => pair(char::from(*a), char::from(*b)),
            _ => (Box::new(|_: &mut [char]| ())),
        })
        .collect()
}

mod tests {
    use rust_aoc::gen_test;
    gen_test!(part1 => None);
    gen_test!(part2 => None);
}
