use crate::Direction::Right;
use itertools::Itertools;
use rust_aoc::solve_and_log;
use std::collections::VecDeque;
use std::ops::Index;
use Direction::{Down, Left, Up};

const YEAR: u16 = 2023;
const DAY: u8 = 16;

fn main() {
    solve_and_log!(part1, part2 with example);
    solve_and_log!(part1, part2);
}

struct CharGrid {
    size: (usize, usize),
    chars: Vec<char>,
}

impl Into<CharGrid> for &str {
    fn into(self) -> CharGrid {
        let chars = self.chars().collect_vec();
        let width = chars.iter().position(|&c| c == '\n').unwrap_or(0);
        let height = chars.len() / (width + 1);
        CharGrid {
            size: (height, width),
            chars,
        }
    }
}

impl Index<(usize, usize)> for CharGrid {
    type Output = char;
    fn index(&self, index: (usize, usize)) -> &Self::Output {
        &self.chars[index.1 * (self.size.1 + 1) + index.0]
    }
}

impl CharGrid {
    fn energize(&self, beam: Beam) -> u32 {
        let mut visited = vec![0u8; self.size.1 * self.size.0];
        let mut visit = |b: &Beam| {
            if b.x == self.size.0 || b.y == self.size.1 {
                return false;
            }
            let bit = match b.d {
                Up => 0b1000,
                Left => 0b0100,
                Down => 0b0010,
                Right => 0b0001,
            };
            let idx = b.y * self.size.1 + b.x;
            let visit = visited[idx] & bit == 0;
            visited[idx] |= bit;
            visit
        };

        let mut pending = VecDeque::new();
        pending.push_back(beam);
        while let Some(mut current) = pending.pop_front() {
            while visit(&current) {
                let tile = self[(current.x, current.y)];
                match current.handle(tile) {
                    (None, _) => break,
                    (Some(next), split) => {
                        if let Some(split) = split {
                            pending.push_back(split)
                        }
                        current = next
                    }
                }
            }
        }

        visited.into_iter()
            .fold(0, |acc, b| if b != 0 { acc + 1 } else { acc })
    }
}

fn part1(input: &str) -> Option<u32> {
    let grid: CharGrid = input.into();
    Some(grid.energize(Beam {
        x: 0,
        y: 0,
        d: Right,
    }))
}

fn part2(input: &str) -> Option<u32> {
    let grid: CharGrid = input.into();
    let (rows, cols) = grid.size;
    let left = (0..rows).map(|y| grid.energize(Beam { x: cols - 1, y, d: Left })).max();
    let right = (0..rows).map(|y| grid.energize(Beam { x: 0, y, d: Right })).max();
    let down = (0..cols).map(|x| grid.energize(Beam { x, y: 0, d: Down })).max();
    let up = (0..cols).map(|x| grid.energize(Beam { x, y: rows - 1, d: Up })).max();
    right.max(left).max(down).max(up)
}

#[derive(Clone, Debug)]
struct Beam {
    x: usize,
    y: usize,
    d: Direction,
}

impl Beam {
    fn handle(mut self, tile: char) -> (Option<Beam>, Option<Beam>) {
        match (tile, &self.d) {
            ('-', Up | Down) | ('|', Left | Right) => {
                let mut split = self.clone();
                self.d = self.d.left();
                split.d = split.d.right();
                return (Some(self), Some(split));
            }
            ('\\', Up | Down) | ('/', Left | Right) => self.d = self.d.left(),
            ('/', Up | Down) | ('\\', Left | Right) => self.d = self.d.right(),
            _ => {}
        };
        (self.forward(), None)
    }

    fn forward(mut self) -> Option<Beam> {
        match self.d {
            Up if self.y > 0 => self.y -= 1,
            Down => self.y += 1,
            Left if self.x > 0 => self.x -= 1,
            Right => self.x += 1,
            _ => return None,
        }
        Some(self)
    }
}

#[derive(Debug, Clone)]
enum Direction {
    Up,
    Left,
    Down,
    Right,
}

impl Direction {
    fn left(&self) -> Self {
        match *self {
            Up => Left,
            Left => Down,
            Down => Right,
            Right => Up,
        }
    }
    fn right(&self) -> Self {
        match *self {
            Left => Up,
            Down => Left,
            Right => Down,
            Up => Right,
        }
    }
}

mod tests {
    use rust_aoc::gen_test;
    gen_test!(part1 => 7632);
    gen_test!(part2 => 8023);
}
