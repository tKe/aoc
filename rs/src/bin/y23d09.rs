use itertools::Itertools;
use rust_aoc::solve_and_log;

const YEAR: u16 = 2023;
const DAY: u8 = 9;

fn main() {
    solve_and_log!(part1, part2);
}

fn next(seq: &mut Vec<i32>) -> i32 {
    let mut next = 0;
    for i in (1..seq.len()).rev() {
        next += seq[i];
        for j in 0..i {
            seq[j] = seq[j + 1] - seq[j]
        }
    }
    next
}

fn part1(input: &str) -> Option<i32> {
    Some(input.split("\n")
        .filter(|s| s.len() > 0)
        .map(|s| {
            let mut v = s.split(' ').filter_map(|n| n.parse::<i32>().ok()).collect_vec();
            next(&mut v)
        })
        .sum())
}

fn part2(input: &str) -> Option<i32> {
    Some(input.split("\n")
        .filter(|s| s.len() > 0)
        .map(|s| {
            let mut v = s.split(' ').filter_map(|n| n.parse::<i32>().ok()).rev().collect_vec();
            next(&mut v)
        })
        .sum())
}

mod tests {
    use rust_aoc::{gen_test_main};
    gen_test_main!(part1 => 1743490457);
    gen_test_main!(part2 => 1053);
}
