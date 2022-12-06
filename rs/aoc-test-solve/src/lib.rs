extern crate proc_macro;
extern crate quote;

use proc_macro::*;
use quote::{format_ident, quote};
use syn::parse_macro_input;

#[proc_macro_attribute]
pub fn test_solve(attr: TokenStream, item: TokenStream) -> TokenStream {
    let args = parse_macro_input!(attr as ::syn::AttributeArgs);
    let exp = &args[0];

    let func = parse_macro_input!(item as ::syn::ItemFn);
    let func_name = func.sig.ident.clone();
    let test_name = format_ident!("test_{}", func_name);

    proc_macro::TokenStream::from(quote! {
        #func

        #[test]
        fn #test_name() {
            use super::{YEAR, DAY};
            use ::rust_aoc::assert_all_eq;
            assert_all_eq!(#func_name with example(YEAR, DAY), #exp);
        }
    })
}
