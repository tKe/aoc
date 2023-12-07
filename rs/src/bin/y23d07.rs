use itertools::Itertools;
use rust_aoc::solve_and_log;

const YEAR: u16 = 2023;
const DAY: u8 = 7;

fn main() {
    solve_and_log!(part1, part2);
}


const CARD_VALUES: &str = "_23456789TJQKA";

fn parse_hand(hand: &str) -> u32 {
    let counts = hand_counts(hand);
    let hand_type = match (counts.iter().max().unwrap(), counts.len()) {
        (5, _) => 6,
        (4, _) => 5,
        (3, 2) => 4,
        (3, _) => 3,
        (2, 3) => 2,
        (2, _) => 1,
        _ => 0,
    };

    hand.chars()
        .fold(hand_type, |acc, c| acc << 4 | CARD_VALUES.find(c).map_or(0, |i| i as u32))
}

fn hand_counts(hand: &str) -> Vec<usize> {
    let mut char_counts = hand.chars().counts();

    // merge wildcards into best count
    match char_counts.remove(&'_') {
        Some(5) => { char_counts.insert('A', 5); }
        Some(wildcards) => {
            if let Some((_, count)) = char_counts.iter_mut()
                .max_by_key(|(&c, cnt)| (**cnt, hand.find(c))) {
                *count += wildcards;
            }
        }
        _ => {}
    }

    char_counts.into_values().collect_vec()
}

fn winnings(input: &str) -> u64 {
    let bids = input.split('\n')
        .filter_map(|s| {
            let (hand, bid) = s.split_once(" ")?;
            Some((parse_hand(hand), bid.parse::<u64>().ok()?))
        })
        .sorted_by_key(|&(hand, _)| hand)
        .map(|(_, bid)| bid);

    let mut i = 0;
    let mut winnings = 0;
    for bid in bids {
        i += 1;
        winnings += i * bid
    }
    winnings
}

fn part1(input: &str) -> Option<u64> {
    Some(winnings(input))
}

fn part2(input: &str) -> Option<u64> {
    Some(winnings(input.replace('J', "_").as_str()))
}

mod tests {
    use rust_aoc::{gen_test_main};
    gen_test_main!(part1 => 248422077);
    gen_test_main!(part2 => 249817836);
}
