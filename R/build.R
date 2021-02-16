if (!require("blogdown")) {
    devtools::install_version("blogdown", version = "0.20", upgrade = FALSE)
    blogdown::install_hugo("0.48", force = TRUE, use_brew = FALSE)
}

blogdown::build_site()
