use itertools::Itertools;
use rust_aoc::solve_and_log;

const YEAR: u16 = 2023;
const DAY: u8 = 6;

fn main() {
    solve_and_log!(part1, part2);
}

fn win_count(time: u64, record: u64) -> u64 {
    let d = time.pow(2) - 4 * record;
    let m = ((time as f64 - (d as f64).sqrt()) / 2f64).ceil() as u64;
    (time - 2 * m) + 1
}

fn part1(input: &str) -> Option<u64> {
    let (time_line, distance_line) = input.split("\n").take(2).collect_tuple()?;
    let times: Vec<u64> = time_line
        .split(" ")
        .filter_map(|s| s.parse::<u64>().ok())
        .collect();
    let distances: Vec<u64> = distance_line
        .split(" ")
        .filter_map(|s| s.parse::<u64>().ok())
        .collect();

    Some(
        times
            .iter()
            .zip(distances)
            .map(|(&t, r)| win_count(t, r))
            .product(),
    )
}

fn part2(input: &str) -> Option<u64> {
    let stripped = input.replace(" ", "");
    let (time, distance) = stripped
        .split("\n")
        .filter_map(|s| s.split_once(":")?.1.parse::<u64>().ok())
        .collect_tuple()?;

    Some(win_count(time, distance))
}

mod tests {
    use rust_aoc::{gen_test_main};
    gen_test_main!(part1 => 170000);
    gen_test_main!(part2 => 20537782);
}
