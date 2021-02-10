if (!require("blogdown")) {
    devtools::install_version("blogdown", version = "0.20")
    blogdown::install_hugo("0.40.3", force = TRUE, use_brew = FALSE)
}

blogdown::build_site()
