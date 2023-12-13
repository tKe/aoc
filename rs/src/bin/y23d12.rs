use std::collections::HashMap;
use std::iter::repeat;
use itertools::Itertools;
use rust_aoc::solve_and_log;

const YEAR: u16 = 2023;
const DAY: u8 = 12;

fn main() {
    solve_and_log!(part1, part2);
}


fn arrangements(record: &str, blocks: Vec<usize>) -> u64 {
    struct F<'a> {
        m: HashMap<(usize, usize), u64>,
        b: Vec<usize>,
        r: &'a str,
    }
    impl F<'_> {
        fn calc(self: &mut Self, ofs: usize, blk: usize, slack: usize) -> u64 {
            if let Some(cached) = self.m.get(&(ofs, blk)) {
                return *cached;
            }

            let more_blocks = blk + 1 < self.b.len();
            let blk_size = self.b[blk];
            let mut sum = 0;
            for gap in 0..=slack {
                let bs = ofs + gap;
                if self.r[ofs..bs].contains('#') { break; }

                let be = bs + blk_size;
                if self.r[bs..be].contains('.') { continue; }
                if more_blocks && self.r[be..=be].contains('#') { continue; }

                sum += if more_blocks {
                    self.calc(be + 1, blk + 1, slack - gap)
                } else if self.r[be..].contains('#') {
                    0
                } else {
                    1
                }
            }

            self.m.insert((ofs, blk), sum);
            sum
        }
    }

    let slack = record.len() - blocks.iter().sum::<usize>() - blocks.len() + 1;
    F { m: HashMap::new(), b: blocks, r: record }.calc(0, 0, slack)
}

fn parse(input: &str) -> impl Iterator<Item=(&str, Vec<usize>)> {
    input.lines()
        .filter_map(|l| l.split_once(' '))
        .map(|(r, c)| (r, c.split(',')
            .filter_map(|n| n.parse::<usize>().ok()).collect_vec())
        )
}

fn part1(input: &str) -> Option<u64> {
    parse(input)
        .map(|(record, blocks)| arrangements(record, blocks))
        .sum1()
}

fn part2(input: &str) -> Option<u64> {
    parse(input)
        .map(|(record, blocks)| {
            let unfolded_record = repeat(record).take(5).join("?");
            let unfolded_blocks = blocks.repeat(5);
            arrangements(unfolded_record.as_str(), unfolded_blocks)
        })
        .sum1()
}

mod tests {
    use rust_aoc::{gen_test_main};
    gen_test_main!(part1 => 7025);
    gen_test_main!(part2 => 11461095383315);
}
