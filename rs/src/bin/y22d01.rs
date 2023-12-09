use rust_aoc::solve_and_log;

const YEAR: u16 = 2022;
const DAY: u8 = 1;

fn main() {
    solve_and_log!(part1, part2);
}

fn part1(input: &str) -> Option<u32> {
    calories(input).iter().max().map(|z| *z)
}

fn part2(input: &str) -> Option<u32> {
    let mut c = calories(input);
    c.sort();
    Some(c.iter().rev().take(3).sum::<u32>())
}

fn calories(input: &str) -> Vec<u32> {
    input.lines().fold(vec![0u32], |acc, line| {
        let v = line.parse::<u32>().ok();
        match (v, acc.as_slice()) {
            (None, rem) => [&[0], rem].concat(),
            (Some(i), [cur, rem @ ..]) => [&[cur + i], rem].concat(),
            _ => panic!("wtf?"),
        }
    })
}

mod tests {
    use rust_aoc::gen_test;
    gen_test!(part1 => 24000);
    gen_test!(part2 => 45000);
}
