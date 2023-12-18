use itertools::Itertools;
use rust_aoc::solve_and_log;

const YEAR: u16 = 2023;
const DAY: u8 = 18;

fn main() {
    solve_and_log!(part1, part2 with example);
    solve_and_log!(part1, part2);
}

fn solve<F>(input: &str, parser: F) -> Option<i64> where F: Fn(&str) -> Option<(i64, i64)> {
    let verts = input.lines().filter_map(parser)
        .scan((0i64, 0i64), |acc, (x, y)| {
            acc.0 += x;
            acc.1 += y;
            Some(*acc)
        })
        .collect_vec();

    let mut lace = 0;
    let mut peri = 0;
    for (a, b, c) in verts.iter().circular_tuple_windows() {
        lace += b.1 * (a.0 - c.0);
        peri += (b.0 - a.0).abs() + (b.1 - a.1).abs();
    }
    Some(lace.abs() / 2 + peri / 2 + 1)
}

fn part1(input: &str) -> Option<i64> {
    solve(input, |it| {
        let (d, u) = it.split(' ').take(2).collect_tuple()?;
        match (d, u.parse::<i64>()) {
            ("R", Ok(n)) => Some((n, 0)),
            ("D", Ok(n)) => Some((0, n)),
            ("L", Ok(n)) => Some((-n, 0)),
            ("U", Ok(n)) => Some((0, -n)),
            _ => None
        }
    })
}

fn part2(input: &str) -> Option<i64> {
    solve(input, |it| {
        let col = i64::from_str_radix(&it.rsplit_once('#')?.1[..6], 16).ok()?;
        match (col & 0xF, col >> 4) {
            (0, n) => Some((n, 0)),
            (1, n) => Some((0, n)),
            (2, n) => Some((-n, 0)),
            (3, n) => Some((0, -n)),
            _ => None
        }
    })
}

mod tests {
    use rust_aoc::gen_test_main;
    gen_test_main!(part1 => 92758);
    gen_test_main!(part2 => 62762509300678);
}
