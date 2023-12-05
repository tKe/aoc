use std::ops::Range;
use itertools::Itertools;
use rust_aoc::solve_and_log;

const YEAR: u16 = 2023;
const DAY: u8 = 5;

fn main() {
    solve_and_log!(part1, part2);
}

trait Mapper {
    fn map(&self, v: u64) -> u64;
    fn map_range(&self, r: Range<u64>) -> Vec<Range<u64>>;
}

type RangeMapper = Vec<(Range<u64>, Range<u64>)>;

impl Mapper for RangeMapper {
    fn map(&self, v: u64) -> u64 {
        self.iter().find(|(src, _)| src.contains(&v))
            .map(|(src, dst)| v - src.start + dst.start)
            .unwrap_or(v)
    }

    fn map_range(&self, r: Range<u64>) -> Vec<Range<u64>> {
        let mut res: Vec<Range<u64>> = vec![];
        let mut rem = r.clone();
        for (src, dst) in self {
            if rem.start >= src.end {
                continue;
            }

            if rem.end <= src.start {
                res.push(rem);
                return res;
            }
            if rem.start < src.start {
                res.push(rem.start..src.start + 1);
                rem.start = src.start
            }

            if src.contains(&(rem.end - 1)) {
                // last range covered
                let start = rem.start - src.start + dst.start;
                let end = rem.end - src.start + dst.start;
                res.push(start..end);
                return res;
            }

            if src.contains(&rem.start) {
                let start = rem.start - src.start + dst.start;
                let end = dst.end;
                res.push(start..end);
                rem.start = src.end
            }
        }


        return res;
    }
}

fn parse(input: &str) -> Option<(Vec<u64>, Vec<RangeMapper>)> {
    let mut sections = input.split("\n\n");

    let seeds = sections.next()?
        .split_once(": ")?
        .1.split(" ").map(|s| s.parse::<u64>().unwrap())
        .collect();

    let mappers: Vec<Vec<(Range<u64>, Range<u64>)>> = sections.map(|section| {
        section.lines().skip(1)
            .map(|s| {
                let (dst, src, len) = s.split(" ")
                    .filter_map(|s| s.parse::<u64>().ok())
                    .collect_tuple().unwrap();
                (src..(src + len), dst..(dst + len))
            })
            .sorted_by_key(|(src, _)| src.start)
            .collect()
    }).collect();

    Some((seeds, mappers))
}

fn part1(input: &str) -> Option<u64> {
    let (seeds, mappers) = parse(input)?;

    let mapped = mappers.iter()
        .fold(seeds, |acc, mapper| {
            acc.iter()
                .map(|&v| mapper.map(v))
                .collect()
        });

    let min = *mapped.iter().min()?;

    Some(min)
}

fn part2(input: &str) -> Option<u64> {
    let (seeds, mappers) = parse(input)?;

    let seed_ranges: Vec<Range<u64>> = seeds.chunks_exact(2).map(|c| c[0]..(c[0] + c[1])).collect();

    let mapped = mappers.iter()
        .fold(seed_ranges, |acc, mapper| {
            acc.iter()
                .flat_map(|r| mapper.map_range(r.start..r.end))
                .collect()
        });

    let min = mapped.iter().map(|r| r.start).min()?;

    Some(min)
}

mod tests {
    use rust_aoc::gen_test_main;
    gen_test_main!(part1 => 84470622);
    gen_test_main!(part2 => 26714516);
}

