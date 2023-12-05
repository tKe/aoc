extern crate proc_macro;

use std::fs;

#[macro_export]
macro_rules! solve_and_log {
    ($($solvers:ident),+) => (solve_and_log!($($solvers),+ with input(YEAR, DAY)));
    ($($solvers:ident),+ with input ($year:expr, $day:expr)) => (solve_and_log!($($solvers),+ with input($year, $day, "input")));
    ($($solvers:ident),+ with input ($year:expr, $day:expr, $input_name:expr)) => {{
        match rust_aoc::read_input($year, $day, $input_name) {
            None => println!("⚠️ Missing input '{}' for Year {} Day {}", $input_name, $year, $day),
            Some(input) => {
                println!("Loaded input '{}' for Year {} Day {}", $input_name, $year, $day);
                solve_and_log!($($solvers),+ with input);
            }
        }
    }};
    ($solver:ident, $($solvers:ident),* with $input:ident) => {
        solve_and_log!($solver with $input);
        solve_and_log!($($solvers),* with $input)
    };
    ($solver:ident with $input:ident) => {{
        use std::time::Instant;

        let timer = Instant::now();
        let result = $solver(&$input);
        let elapsed = timer.elapsed();
        match result {
            Some(result) =>
                println!("  {} (took {:.2?}): {}", stringify!($solver), elapsed, result),
            None =>
                println!("  {} not solved", stringify!($solver))
        };
    }};
}

#[macro_export]
macro_rules! assert_all_eq {
    ($($solvers:ident),+ with example ($year:expr, $day:expr), $expected:expr) => {
        assert_all_eq!($($solvers),+ with input($year, $day, "example"), $expected);
    };
    ($($solvers:ident),+ with input ($year:expr, $day:expr, $input_name:expr), $expected:expr) => {{
        match rust_aoc::read_input($year, $day, $input_name) {
            None => panic!("⚠️ Missing input '{}' for Year {} Day {}", $input_name, $year, $day),
            Some(input) => {
                assert_all_eq!($($solvers),+ with input, $expected);
            }
        }
    }};
    ($solver:ident, $($solvers:ident),* with $input:ident, $expected:expr) => {
        assert_all_eq!($solver with $input, $expected);
        assert_all_eq!($($solvers),* with $input, $expected)
    };
    ($solver:ident with $input:ident, $expected:expr) => {{
        let result = $solver(&$input);
        assert_eq!(result, Some($expected))
    }};
}

#[macro_export]
macro_rules! gen_test {
    ($solver:ident => $expected:expr) => {
        #[test]
        fn $solver() {
            use super::*;
            use ::rust_aoc::assert_all_eq;
            assert_all_eq!($solver with example(YEAR, DAY), $expected);
        }
    };
}

#[macro_export]
macro_rules! gen_test_main {
    ($solver:ident => $expected:expr) => {
        #[test]
        fn $solver() {
            use super::*;
            use ::rust_aoc::assert_all_eq;
            assert_all_eq!($solver with input(YEAR, DAY, "input"), $expected);
        }
    };
}

#[macro_export]
macro_rules! gen_tests {
    ($($solver:ident),+ => $expected:expr) => ($(gen_test!($solver => $expected);)+)
}

pub fn read_input(year: u16, day: u8, name: &str) -> Option<String> {
    // todo: find inputs folder
    let path = format!("../inputs/year-{}/day-{:02}/{}.txt", year, day, name);
    fs::read_to_string(&path).ok()
}
