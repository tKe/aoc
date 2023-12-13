use std::ops::Index;
use itertools::Itertools;
use rust_aoc::solve_and_log;

const YEAR: u16 = 2023;
const DAY: u8 = 10;

fn main() {
    solve_and_log!(part1, part2);
}

struct Point2D { x: u32, y: u32 }

struct Grid { content: Vec<char>, stride: usize, }

impl Grid {
    fn of(input: &str) -> Self {
        let content = input.lines().flat_map(|s|s.chars()).collect_vec();
        let stride = input.find('\n').unwrap_or(content.len());
        Grid { content, stride }
    }
}

impl Index<[usize;2]> for Grid {
    type Output = Option<char>;

    fn index(&self, index: [usize; 2]) -> &Self::Output {
        let [x, y] = index;
        let found = self.content[y * self.stride + x];
        Some(found)
    }
}

fn part1(input: &str) -> Option<i32> {
    let grid = Grid::of(input);
    todo!()
}

fn part2(input: &str) -> Option<i32> {
    todo!()
}

mod tests {
    use rust_aoc::{gen_test_main};
    gen_test_main!(part1 => 6903);
    gen_test_main!(part2 => 265);
}
