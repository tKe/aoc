use itertools::Itertools;
use rust_aoc::solve_and_log;
use std::{
    collections::hash_map::DefaultHasher,
    hash::{Hash, Hasher},
};

const YEAR: u16 = 2023;
const DAY: u8 = 14;

fn main() {
    solve_and_log!(part1, part2 with example);
    solve_and_log!(part1, part2);
}

fn part1(input: &str) -> Option<u32> {
    Some(CharGrid::parse(input).ok()?.rotate_and_roll().right_load())
}

fn part2(input: &str) -> Option<u32> {
    let mut grid = CharGrid::parse(input).ok()?;

    let mut hist = Vec::new();
    loop {
        grid = grid
            .rotate_and_roll()
            .rotate_and_roll()
            .rotate_and_roll()
            .rotate_and_roll();

        let mut hasher = DefaultHasher::new();
        grid.grid.hash(&mut hasher);
        let hash = hasher.finish();

        let key = (hash, grid.top_load());
        match hist.iter().rposition(|k: &(u64, u32)| *k == key) {
            Some(loop_start) => {
                let cycle = hist.len() - loop_start;
                let remaining = 1_000_000_000 - hist.len() - 1;
                return Some(hist[loop_start..][remaining % cycle].1);
            }
            None => hist.push(key),
        }
    }
}

struct CharGrid {
    grid: Vec<Vec<char>>,
    dim: usize,
}

impl CharGrid {
    fn parse(input: &str) -> Result<Self, &str> {
        let grid = input
            .lines()
            .map(|line| line.chars().collect_vec())
            .collect_vec();
        let dim = grid.len();
        if grid.iter().all(|r| r.len() == dim) {
            Ok(CharGrid { dim, grid })
        } else {
            Err("not a square grid")
        }
    }

    fn rotate_and_roll(&self) -> CharGrid {
        let grid = (0..self.dim)
            .map(|x| {
                let mut row = (0..self.dim)
                    .map(|y| self.grid[self.dim - 1 - y][x])
                    .collect_vec();
                row.split_mut(|&c| c == '#').for_each(<[char]>::sort);
                row
            })
            .collect_vec();
        CharGrid {
            dim: self.dim,
            grid,
        }
    }

    fn right_load(&self) -> u32 {
        self.grid
            .iter()
            .map(|r| {
                r.iter()
                    .positions(|&c| c == 'O')
                    .map(|x| x as u32 + 1)
                    .sum::<u32>()
            })
            .sum()
    }

    fn top_load(&self) -> u32 {
        self.grid
            .iter()
            .enumerate()
            .map(|(y, r)| r.iter().filter(|&c| *c == 'O').count() * (self.dim - y))
            .sum::<usize>() as u32
    }
}

mod tests {
    use rust_aoc::gen_test_main;
    gen_test_main!(part1 => 110779);
    gen_test_main!(part2 => 86069);
}
