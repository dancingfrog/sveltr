#!/usr/bin/env Rscript

print(country)
print(current_version)
print(previous_version)
print(state_codes)
print(state_flag)

length_units <- c()
switch(country,
    "GBR" = { length_units <- c("miles") },
    "USA" = { length_units <- c("miles") },
    { length_units <- c("kilometers") }
)

loadStatsCsv <- function (version, country, code, layer) {
    stats_file_path <- paste0(working_dir, "/", version, "/", country, "/", stringr::str_to_lower(code), layer, "-statistics.csv")
    stats <- data.frame(
        "Table/Attribute" = c(NA),
        "Count" = c(NA),
        "Attribute Filled %" = c(NA)
    )

    if (all(state_flag)) {
        state_code <- switch(
            layer,
            "_nodes" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_restrictions" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_streets" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_speed_profile_sets" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            { stringr::str_to_lower(state_codes[[1]]) }
        )
        stats_file_path <- paste0(working_dir, "/", version, "/", country, "/", state_code, layer, "-statistics.csv")

        for (stats_file in stats_file_path) {
            if (file.exists(stats_file)) {
                print(stats_file)
                stats <- read.csv(stats_file, sep = ",", na.strings = "NA", strip.white = TRUE, stringsAsFactors = FALSE)
            }
        }

    } else if (file.exists(stats_file_path)) {
        stats_file <- stats_file_path
        #print(stats_file)
        stats <- read.csv(stats_file, sep = ",", na.strings = "NA", strip.white = TRUE, stringsAsFactors = FALSE)

    } else if ((!is.null(state_codes)) && (!is.na(state_codes)) && (country_code3 == "usa" || country_code3 == "can")) {
        nation_wide_frame <- c(NA)
        lengths_frame <- c(NA)

        state_codes <- switch(
            layer,
            "_nodes" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_restrictions" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_streets" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_speed_profile_sets" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            { stringr::str_to_lower(state_codes[[1]]) }
        )
        stats_file_paths <- paste0(working_dir, "/", version, "/", country, "/", state_codes, layer, "-statistics.csv")

        for (stats_file in stats_file_paths) {
            if (file.exists(stats_file)) {

                if (!is.na(nation_wide_frame) && !is.null(nation_wide_frame)) {
                    # print(stats_file)
                    read_frame <- read.csv(stats_file, sep = ",", na.strings = "NA", strip.white = TRUE, stringsAsFactors = FALSE)
                    nation_wide_frame[nrow(nation_wide_frame)+1,] <- read_frame[1, 1:3]
                    if (!is.null(read_frame[2, 1:2]) && !is.na(read_frame[2, 1:2])) {
                        lengths_frame[nrow(lengths_frame)+1,] <- read_frame[2, 1:3]
                    }

                } else {
                    # print(stats_file)
                    read_frame <- read.csv(stats_file, sep = ",", na.strings = "NA", strip.white = TRUE, stringsAsFactors = FALSE)
                    nation_wide_frame <- read_frame[1, 1:3]
                    if (!is.null(read_frame[2, 1:2]) && !is.na(read_frame[2, 1:2])) {
                        lengths_frame <- read_frame[2, 1:3]
                    }

                }
            }
        }

        str(nation_wide_frame)

        if (!is.na(lengths_frame) && !is.null(lengths_frame)) {
            str(lengths_frame)
            nation_wide_frame <- rbind(
                nation_wide_frame[-1,1:length(nation_wide_frame)],
                c(paste0(stringr::str_to_lower(code), layer), sum(nation_wide_frame[, 2]), NA),
                c("Length_Meters (total)", sum(lengths_frame[, 2]), NA),
                stringsAsFactors = FALSE
            )
            stats <- nation_wide_frame[c(nrow(nation_wide_frame) - 1, nrow(nation_wide_frame)), ]

        } else if (length(nation_wide_frame) > 2 && length(nation_wide_frame[,2]) > 1) {
            nation_wide_frame <- rbind(
                nation_wide_frame[-1,1:length(nation_wide_frame)],
                c(paste0(stringr::str_to_lower(code), layer), sum(nation_wide_frame[, 2]), NA),
                stringsAsFactors = FALSE
            )
            stats <- nation_wide_frame[nrow(nation_wide_frame), ]
        }
    }

    head(stats)
    stats
}

loadUSCANStatsCsv <- function (version, country, code, layer) {
    stats_file_path <- paste0(working_dir, "/", version, "/", country, "/", stringr::str_to_lower(code), layer, "-statistics.csv")
    stats <- data.frame(
        "Table/Attribute" = c(NA),
        "Count" = c(NA),
        "Attribute Filled %" = c(NA)
    )

    # print(all(state_flag))
    if (all(state_flag)) {
        state_code <- switch(
            layer,
            "_nodes" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_restrictions" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_streets" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            "_speed_profile_sets" = { paste0(stringr::str_to_lower(code), "_", stringr::str_to_lower(state_codes[[1]])) },
            { stringr::str_to_lower(state_codes[[1]]) }
        )
        stats_file_path <- paste0(working_dir, "/", version, "/", country, "/", state_code, layer, "-statistics.csv")
        for (stats_file in stats_file_path) {
            if (file.exists(stats_file)) {
                print(stats_file)
                stats <- read.csv(stats_file, sep = ",", na.strings = "NA", strip.white = TRUE, stringsAsFactors = FALSE)
            }
        }

    } else if (file.exists(stats_file_path)) {
        stats_file <- stats_file_path
        # print(stats_file)
        stats <- read.csv(stats_file, sep = ",", na.strings = "NA", strip.white = TRUE, stringsAsFactors = FALSE)

    } else {
        nation_wide_frame <- c(NA)
        lengths_frame <- c(NA)

        for (state_code in stringr::str_to_lower(state_codes[[1]])) {
            stats_file_path <- paste0(working_dir, "/", version, "/", country, "/", state_code, layer, "-statistics.csv")
            # print(state_code)
            # print(paste0(working_dir, "/", current_version, "/", country, "/", state_code, layer, "-statistics.csv"))

            if (file.exists(stats_file_path)) {
                stats_file <- stats_file_path

                if (!is.na(nation_wide_frame) && !is.null(nation_wide_frame)) {
                    # print(stats_file)
                    read_frame <- read.csv(stats_file, sep = ",", na.strings = "NA", strip.white = TRUE, stringsAsFactors = FALSE)
                    nation_wide_frame[nrow(nation_wide_frame)+1,] <- read_frame[1, 1:3]
                    if (!is.null(read_frame[2, 1:2]) && !is.na(read_frame[2, 1:2])) {
                        lengths_frame[nrow(lengths_frame)+1,] <- read_frame[2, 1:3]
                    }

                } else {
                    # print(stats_file)
                    read_frame <- read.csv(stats_file, sep = ",", na.strings = "NA", strip.white = TRUE, stringsAsFactors = FALSE)
                    nation_wide_frame <- read_frame[1, 1:3]
                    if (!is.null(read_frame[2, 1:2]) && !is.na(read_frame[2, 1:2])) {
                        lengths_frame <- read_frame[2, 1:3]
                    }

                }
            }
        }

        str(nation_wide_frame)

        #if (length(nation_wide_frame) > 2 && length(nation_wide_frame[,2]) > 1) {
        #     print(length(nation_wide_frame[,2]))
        #     print(paste0(stringr::str_to_lower(code), layer, " ", sum(nation_wide_frame[, 2])))
        #}

        if (!is.na(lengths_frame) && !is.null(lengths_frame)) {
            str(lengths_frame)
            nation_wide_frame <- rbind(
                nation_wide_frame[-1,1:length(nation_wide_frame)],
                c(paste0(stringr::str_to_lower(code), layer), sum(nation_wide_frame[, 2]), NA),
                c("Length_Meters (total)", sum(lengths_frame[, 2]), NA),
                stringsAsFactors = FALSE
            )
            stats <- nation_wide_frame[c(nrow(nation_wide_frame) - 1, nrow(nation_wide_frame)), ]

        } else if (length(nation_wide_frame) > 2 && length(nation_wide_frame[,2]) > 1) {
            nation_wide_frame <- rbind(
                nation_wide_frame[-1,1:length(nation_wide_frame)],
                c(paste0(stringr::str_to_lower(code), layer), sum(nation_wide_frame[, 2]), NA),
                stringsAsFactors = FALSE
            )
            stats <- nation_wide_frame[nrow(nation_wide_frame), ]
        }
    }

    head(stats)
    stats
}

# Navigation Tables
nodes_current <- loadStatsCsv(current_version, country, country_code3, "_nodes")
nodes_previous <- loadStatsCsv(previous_version, country, country_code3, "_nodes")

head(nodes_current)
head(nodes_previous)

restrictions_current <- loadStatsCsv(current_version, country, country_code3, "_restrictions")
restrictions_previous <- loadStatsCsv(previous_version, country, country_code3, "_restrictions")

head(restrictions_current)
head(restrictions_previous)

streets_current <- loadStatsCsv(current_version, country, country_code3, "_streets")
streets_previous <- loadStatsCsv(previous_version, country, country_code3, "_streets")

head(streets_current)
head(streets_previous)

# Speed Profiles Table
speed_profile_sets_current <- loadStatsCsv(current_version, country, country_code3, "_speed_profile_sets")
speed_profile_sets_previous <- loadStatsCsv(previous_version, country, country_code3, "_speed_profile_sets")

head(speed_profile_sets_current)
head(speed_profile_sets_previous)

if ((!is.null(state_codes)) && (!is.na(state_codes)) && (country_code3 == "usa" || country_code3 == "can")) {
    # US/CAN Classic Tables
    crossroads_current <- loadUSCANStatsCsv(current_version, country, country_code3, "crossroads")
    crossroads_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "crossroads")
    geocode1_current <- loadUSCANStatsCsv(current_version, country, country_code3, "geocode1")
    geocode1_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "geocode1")
    geocode2_current <- loadUSCANStatsCsv(current_version, country, country_code3, "geocode2")
    geocode2_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "geocode2")
    sa_current <- loadUSCANStatsCsv(current_version, country, country_code3, "sa")
    sa_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "sa")

    # US/CAN Display Tables
    airports_current <- loadUSCANStatsCsv(current_version, country, country_code3, "airports")
    airports_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "airports")

    cities_current <- loadUSCANStatsCsv(current_version, country, country_code3, "cities")
    cities_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "cities")

    expressways_current <- loadUSCANStatsCsv(current_version, country, country_code3, "expressways")
    expressways_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "expressways")

    ferries_current <- loadUSCANStatsCsv(current_version, country, country_code3, "ferries")
    ferries_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "ferries")

    gazetteer1_current <- loadUSCANStatsCsv(current_version, country, country_code3, "gazetteer1")
    gazetteer1_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "gazetteer1")

    gazetteer2_current <- loadUSCANStatsCsv(current_version, country, country_code3, "gazetteer2")
    gazetteer2_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "gazetteer2")

    gazetteer3_current <- loadUSCANStatsCsv(current_version, country, country_code3, "gazetteer3")
    gazetteer3_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "gazetteer3")

    gazetteer4_current <- loadUSCANStatsCsv(current_version, country, country_code3, "gazetteer4")
    gazetteer4_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "gazetteer4")

    gazetteer5_current <- loadUSCANStatsCsv(current_version, country, country_code3, "gazetteer5")
    gazetteer5_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "gazetteer5")

    gazetteer6_current <- loadUSCANStatsCsv(current_version, country, country_code3, "gazetteer6")
    gazetteer6_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "gazetteer6")

    gazetteer7_current <- loadUSCANStatsCsv(current_version, country, country_code3, "gazetteer7")
    gazetteer7_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "gazetteer7")

    landmarks_current <- loadUSCANStatsCsv(current_version, country, country_code3, "landmarks")
    landmarks_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "landmarks")

    landuse_current <- loadUSCANStatsCsv(current_version, country, country_code3, "landuse")
    landuse_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "landuse")

    localhwys_med_current <- loadUSCANStatsCsv(current_version, country, country_code3, "localhwys_med")
    localhwys_med_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "localhwys_med")

    localrtes_current <- loadUSCANStatsCsv(current_version, country, country_code3, "localrtes")
    localrtes_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "localrtes")

    oneways_current <- loadUSCANStatsCsv(current_version, country, country_code3, "oneways")
    oneways_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "oneways")

    primaryhwys_current <- loadUSCANStatsCsv(current_version, country, country_code3, "primaryhwys")
    primaryhwys_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "primaryhwys")

    primaryhwys_med_current <- loadUSCANStatsCsv(current_version, country, country_code3, "primaryhwys_med")
    primaryhwys_med_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "primaryhwys_med")

    railroads_current <- loadUSCANStatsCsv(current_version, country, country_code3, "railroads")
    railroads_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "railroads")

    regionalhwys_current <- loadUSCANStatsCsv(current_version, country, country_code3, "regionalhwys")
    regionalhwys_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "regionalhwys")

    rivers_current <- loadUSCANStatsCsv(current_version, country, country_code3, "rivers")
    rivers_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "rivers")

    secondaryhwys_current <- loadUSCANStatsCsv(current_version, country, country_code3, "secondaryhwys")
    secondaryhwys_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "secondaryhwys")

    secondaryhwys_med_current <- loadUSCANStatsCsv(current_version, country, country_code3, "secondaryhwys_med")
    secondaryhwys_med_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "secondaryhwys_med")

    signposts_current <- loadUSCANStatsCsv(current_version, country, country_code3, "signposts")
    signposts_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "signposts")

    displaystreets_current <- loadUSCANStatsCsv(current_version, country, country_code3, "streets")
    displaystreets_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "streets")

    urbanareas_current <- loadUSCANStatsCsv(current_version, country, country_code3, "urbanareas")
    urbanareas_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "urbanareas")

    waterbodies_current <- loadUSCANStatsCsv(current_version, country, country_code3, "waterbodies")
    waterbodies_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "waterbodies")

}

if (!is.null(country_code3[[1]]) && !is.na(country_code3) && (country_code3 == "usa")) {
    # US only

    counties_current <- loadUSCANStatsCsv(current_version, country, country_code3, "counties")
    counties_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "counties")

    shldco_0to5_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldco_0to5")
    shldco_0to5_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldco_0to5")

    shldco_5to15_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldco_5to15")
    shldco_5to15_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldco_5to15")

    shldco_15to50_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldco_15to50")
    shldco_15to50_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldco_15to50")

    shldinter_0to5_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldinter_0to5")
    shldinter_0to5_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldinter_0to5")

    shldinter_5to15_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldinter_5to15")
    shldinter_5to15_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldinter_5to15")

    shldinter_15to50_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldinter_15to50")
    shldinter_15to50_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldinter_15to50")

    shldstate_0to5_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldstate_0to5")
    shldstate_0to5_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldstate_0to5")

    shldstate_5to15_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldstate_5to15")
    shldstate_5to15_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldstate_5to15")

    shldstate_15to50_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldstate_15to50")
    shldstate_15to50_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldstate_15to50")

    shldus_0to5_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldus_0to5")
    shldus_0to5_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldus_0to5")

    shldus_5to15_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldus_5to15")
    shldus_5to15_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldus_5to15")

    shldus_15to50_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldus_15to50")
    shldus_15to50_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldus_15to50")

    towns_current <- loadUSCANStatsCsv(current_version, country, country_code3, "towns")
    towns_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "towns")
}

if (!is.null(country_code3[[1]]) && !is.na(country_code3) && (country_code3 == "can")) {
    # CAN only

    censusdivisions_current <- loadUSCANStatsCsv(current_version, country, country_code3, "censusdivisions")
    censusdivisions_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "censusdivisions")

    censussubdivisions_current <- loadUSCANStatsCsv(current_version, country, country_code3, "censussubdivisions")
    censussubdivisions_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "censussubdivisions")

    shldprhwy_0to5_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldprhwy_0to5")
    shldprhwy_0to5_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldprhwy_0to5")

    shldprhwy_5to15_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldprhwy_5to15")
    shldprhwy_5to15_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldprhwy_5to15")

    shldprhwy_15to50_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldprhwy_15to50")
    shldprhwy_15to50_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldprhwy_15to50")

    shldtchwy_0to5_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldtchwy_0to5")
    shldtchwy_0to5_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldtchwy_0to5")

    shldtchwy_5to15_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldtchwy_5to15")
    shldtchwy_5to15_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldtchwy_5to15")

    shldtchwy_15to50_current <- loadUSCANStatsCsv(current_version, country, country_code3, "shldtchwy_15to50")
    shldtchwy_15to50_previous <- loadUSCANStatsCsv(previous_version, country, country_code3, "shldtchwy_15to50")
}

if (!is.null(country_code4[[1]]) && !is.na(country_code4)) {
    # EMEA Classic Tables
    cr_current <- loadStatsCsv(current_version, country, country_code4, "cr")
    cr_previous <- loadStatsCsv(previous_version, country, country_code4, "cr")
    sa_current <- loadStatsCsv(current_version, country, country_code4, "sa")
    sa_previous <- loadStatsCsv(previous_version, country, country_code4, "sa")
    x1_current <- loadStatsCsv(current_version, country, country_code4, "x1")
    x1_previous <- loadStatsCsv(previous_version, country, country_code4, "x1")
    x2_current <- loadStatsCsv(current_version, country, country_code4, "x2")
    x2_previous <- loadStatsCsv(previous_version, country, country_code4, "x2")

    # EMEA Display Tables
    a0_current <- loadStatsCsv(current_version, country, country_code4, "a0")
    a0_previous <- loadStatsCsv(previous_version, country, country_code4, "a0")

    a1_current <- loadStatsCsv(current_version, country, country_code4, "a1")
    a1_previous <- loadStatsCsv(previous_version, country, country_code4, "a1")

    a2_current <- loadStatsCsv(current_version, country, country_code4, "a2")
    a2_previous <- loadStatsCsv(previous_version, country, country_code4, "a2")

    a3_current <- loadStatsCsv(current_version, country, country_code4, "a3")
    a3_previous <- loadStatsCsv(previous_version, country, country_code4, "a3")

    a4_current <- loadStatsCsv(current_version, country, country_code4, "a4")
    a4_previous <- loadStatsCsv(previous_version, country, country_code4, "a4")

    a5_current <- loadStatsCsv(current_version, country, country_code4, "a5")
    a5_previous <- loadStatsCsv(previous_version, country, country_code4, "a5")

    aa_current <- loadStatsCsv(current_version, country, country_code4, "aa")
    aa_previous <- loadStatsCsv(previous_version, country, country_code4, "aa")

    ap_current <- loadStatsCsv(current_version, country, country_code4, "ap")
    ap_previous <- loadStatsCsv(previous_version, country, country_code4, "ap")

    b_current <- loadStatsCsv(current_version, country, country_code4, "b")
    b_previous <- loadStatsCsv(previous_version, country, country_code4, "b")

    f_current <- loadStatsCsv(current_version, country, country_code4, "f")
    f_previous <- loadStatsCsv(previous_version, country, country_code4, "f")

    fi_current <- loadStatsCsv(current_version, country, country_code4, "fi")
    fi_previous <- loadStatsCsv(previous_version, country, country_code4, "fi")

    fr_current <- loadStatsCsv(current_version, country, country_code4, "fr")
    fr_previous <- loadStatsCsv(previous_version, country, country_code4, "fr")

    g1_current <- loadStatsCsv(current_version, country, country_code4, "g1")
    g1_previous <- loadStatsCsv(previous_version, country, country_code4, "g1")

    g2_current <- loadStatsCsv(current_version, country, country_code4, "g2")
    g2_previous <- loadStatsCsv(previous_version, country, country_code4, "g2")

    g3_current <- loadStatsCsv(current_version, country, country_code4, "g3")
    g3_previous <- loadStatsCsv(previous_version, country, country_code4, "g3")

    g4_current <- loadStatsCsv(current_version, country, country_code4, "g4")
    g4_previous <- loadStatsCsv(previous_version, country, country_code4, "g4")

    g5_current <- loadStatsCsv(current_version, country, country_code4, "g5")
    g5_previous <- loadStatsCsv(previous_version, country, country_code4, "g5")

    g6_current <- loadStatsCsv(current_version, country, country_code4, "g6")
    g6_previous <- loadStatsCsv(previous_version, country, country_code4, "g6")

    gf_current <- loadStatsCsv(current_version, country, country_code4, "gf")
    gf_previous <- loadStatsCsv(previous_version, country, country_code4, "gf")

    lc_current <- loadStatsCsv(current_version, country, country_code4, "lc")
    lc_previous <- loadStatsCsv(previous_version, country, country_code4, "lc")

    lu_current <- loadStatsCsv(current_version, country, country_code4, "lu")
    lu_previous <- loadStatsCsv(previous_version, country, country_code4, "lu")

    nf_current <- loadStatsCsv(current_version, country, country_code4, "nf")
    nf_previous <- loadStatsCsv(previous_version, country, country_code4, "nf")

    ow_current <- loadStatsCsv(current_version, country, country_code4, "ow")
    ow_previous <- loadStatsCsv(previous_version, country, country_code4, "ow")

    p_current <- loadStatsCsv(current_version, country, country_code4, "p")
    p_previous <- loadStatsCsv(previous_version, country, country_code4, "p")

    pc_current <- loadStatsCsv(current_version, country, country_code4, "pc")
    pc_previous <- loadStatsCsv(previous_version, country, country_code4, "pc")

    pci_current <- loadStatsCsv(current_version, country, country_code4, "pci")
    pci_previous <- loadStatsCsv(previous_version, country, country_code4, "pci")

    pcp_current <- loadStatsCsv(current_version, country, country_code4, "pcp")
    pcp_previous <- loadStatsCsv(previous_version, country, country_code4, "pcp")

    pu_current <- loadStatsCsv(current_version, country, country_code4, "pu")
    pu_previous <- loadStatsCsv(previous_version, country, country_code4, "pu")

    r_current <- loadStatsCsv(current_version, country, country_code4, "r")
    r_previous <- loadStatsCsv(previous_version, country, country_code4, "r")

    re_current <- loadStatsCsv(current_version, country, country_code4, "re")
    re_previous <- loadStatsCsv(previous_version, country, country_code4, "re")

    s1_current <- loadStatsCsv(current_version, country, country_code4, "s1")
    s1_previous <- loadStatsCsv(previous_version, country, country_code4, "s1")

    s2_current <- loadStatsCsv(current_version, country, country_code4, "s2")
    s2_previous <- loadStatsCsv(previous_version, country, country_code4, "s2")

    s3_current <- loadStatsCsv(current_version, country, country_code4, "s3")
    s3_previous <- loadStatsCsv(previous_version, country, country_code4, "s3")

    s4_current <- loadStatsCsv(current_version, country, country_code4, "s4")
    s4_previous <- loadStatsCsv(previous_version, country, country_code4, "s4")

    s5_current <- loadStatsCsv(current_version, country, country_code4, "s5")
    s5_previous <- loadStatsCsv(previous_version, country, country_code4, "s5")

    s6_current <- loadStatsCsv(current_version, country, country_code4, "s6")
    s6_previous <- loadStatsCsv(previous_version, country, country_code4, "s6")

    st_current <- loadStatsCsv(current_version, country, country_code4, "st")
    st_previous <- loadStatsCsv(previous_version, country, country_code4, "st")

    u_current <- loadStatsCsv(current_version, country, country_code4, "u")
    u_previous <- loadStatsCsv(previous_version, country, country_code4, "u")

    wm_current <- loadStatsCsv(current_version, country, country_code4, "wm")
    wm_previous <- loadStatsCsv(previous_version, country, country_code4, "wm")

    w_current <- loadStatsCsv(current_version, country, country_code4, "w")
    w_previous <- loadStatsCsv(previous_version, country, country_code4, "w")

    zl_current <- loadStatsCsv(current_version, country, country_code4, "zl")
    zl_previous <- loadStatsCsv(previous_version, country, country_code4, "zl")

    zp_current <- loadStatsCsv(current_version, country, country_code4, "zp")
    zp_previous <- loadStatsCsv(previous_version, country, country_code4, "zp")
}


# Navigation Field/Values
nodes_total <- c(current = NA, previous = NA)
restrictions_total <- c(current = NA, previous = NA)
streets_total <- c(current = NA, previous = NA)
STREET <- c(current = NA, previous = NA)
STREET2 <- c(current = NA, previous = NA)
STREET3 <- c(current = NA, previous = NA)
STREET4 <- c(current = NA, previous = NA)
FROMLEFT <- c(current = NA, previous = NA)
TOLEFT <- c(current = NA, previous = NA)
FROMRIGHT <- c(current = NA, previous = NA)
TORIGHT <- c(current = NA, previous = NA)
L_STRUCT_1 <- c(current = NA, previous = NA)
L_STRUCT_2 <- c(current = NA, previous = NA)
L_STRUCT_3 <- c(current = NA, previous = NA)
L_STRUCT_4 <- c(current = NA, previous = NA)
L_STRUCT_5 <- c(current = NA, previous = NA)
L_STRUCT_6 <- c(current = NA, previous = NA)
R_STRUCT_1 <- c(current = NA, previous = NA)
R_STRUCT_2 <- c(current = NA, previous = NA)
R_STRUCT_3 <- c(current = NA, previous = NA)
R_STRUCT_4 <- c(current = NA, previous = NA)
R_STRUCT_5 <- c(current = NA, previous = NA)
R_STRUCT_6 <- c(current = NA, previous = NA)
A1_LEFT <- c(current = NA, previous = NA)
A1_RIGHT <- c(current = NA, previous = NA)
LOCALITY_LEFT <- c(current = NA, previous = NA)
LOCALITY_CODE_LEFT <- c(current = NA, previous = NA)
LOCALITY_RIGHT <- c(current = NA, previous = NA)
LOCALITY_CODE_RIGHT <- c(current = NA, previous = NA)
PC_LEFT <- c(current = NA, previous = NA)
PNAM_LEFT <- c(current = NA, previous = NA)
PC_RIGHT <- c(current = NA, previous = NA)
PNAM_RIGHT <- c(current = NA, previous = NA)
ROUTE_NUM <- c(current = NA, previous = NA)
AREA_TYPE_0 <- c(current = NA, previous = NA)
AREA_TYPE_1 <- c(current = NA, previous = NA)
ROAD_TYPE_D <- c(current = NA, previous = NA)
ROAD_TYPE_S <- c(current = NA, previous = NA)
SPEED <- c(current = NA, previous = NA)
SPEED_VERIFIED <- c(current = NA, previous = NA)
SPEED_AMPEAK <- c(current = NA, previous = NA)
SPEED_PMPEAK <- c(current = NA, previous = NA)
SPEED_INTERPEAK <- c(current = NA, previous = NA)
SPEED_NIGHT <- c(current = NA, previous = NA)
SPEED_SEVENDAY <- c(current = NA, previous = NA)
ROUGHRD <- c(current = NA, previous = NA)
SURFACE_TYPE_0 <- c(current = NA, previous = NA)
SURFACE_TYPE_1 <- c(current = NA, previous = NA)
SURFACE_TYPE_2 <- c(current = NA, previous = NA)
SURFACE_TYPE_3 <- c(current = NA, previous = NA)
MAX_HEIGHT <- c(current = NA, previous = NA)
MAX_WIDTH <- c(current = NA, previous = NA)
MAX_WEIGHT <- c(current = NA, previous = NA)
ONEWAY_1 <- c(current = NA, previous = NA)
ONEWAY_2 <- c(current = NA, previous = NA)
ONEWAY_3 <- c(current = NA, previous = NA)
ONEWAY_4 <- c(current = NA, previous = NA)
ONEWAY_5 <- c(current = NA, previous = NA)
ONEWAY_6 <- c(current = NA, previous = NA)
TOLL_false <- c(current = NA, previous = NA)
TOLL_true <- c(current = NA, previous = NA)
Length_total <- c(current = NA, previous = NA)
Length_A <- c(current = NA, previous = NA)
Length_C <- c(current = NA, previous = NA)
Length_D <- c(current = NA, previous = NA)
Length_E <- c(current = NA, previous = NA)
Length_F <- c(current = NA, previous = NA)
Length_G <- c(current = NA, previous = NA)
Length_H <- c(current = NA, previous = NA)
Length_I <- c(current = NA, previous = NA)
Length_L <- c(current = NA, previous = NA)
Length_M <- c(current = NA, previous = NA)
Length_N <- c(current = NA, previous = NA)
Length_P <- c(current = NA, previous = NA)
Length_Q <- c(current = NA, previous = NA)
Length_R <- c(current = NA, previous = NA)
Length_S <- c(current = NA, previous = NA)
Length_T <- c(current = NA, previous = NA)
Length_U <- c(current = NA, previous = NA)
Length_W <- c(current = NA, previous = NA)
Length_Z <- c(current = NA, previous = NA)
streetsValuesList <- list(
  streets_total,
  STREET, STREET2, STREET3, STREET4,
  FROMLEFT, TOLEFT, FROMRIGHT, TORIGHT,
  L_STRUCT_1, L_STRUCT_2, L_STRUCT_3,
  L_STRUCT_4, L_STRUCT_5, L_STRUCT_6,
  R_STRUCT_1, R_STRUCT_2, R_STRUCT_3,
  R_STRUCT_4, R_STRUCT_5, R_STRUCT_6,
  A1_LEFT, A1_RIGHT, LOCALITY_LEFT, LOCALITY_CODE_LEFT,
  LOCALITY_RIGHT, LOCALITY_CODE_RIGHT,
  PC_LEFT, PNAM_LEFT, PC_RIGHT, PNAM_RIGHT,
  AREA_TYPE_0, AREA_TYPE_1, ROAD_TYPE_D, ROAD_TYPE_S,
  SPEED, SPEED_VERIFIED, SPEED_AMPEAK, SPEED_PMPEAK,
  SPEED_INTERPEAK, SPEED_NIGHT, SPEED_SEVENDAY,
  ROUGHRD, SURFACE_TYPE_0, SURFACE_TYPE_1, SURFACE_TYPE_2,
  SURFACE_TYPE_3, MAX_HEIGHT, MAX_WIDTH, MAX_WEIGHT,
  ONEWAY_1, ONEWAY_2, ONEWAY_3, ONEWAY_4, ONEWAY_5, ONEWAY_6,
  ROUTE_NUM, TOLL_false, TOLL_true, Length_total, Length_A,
  Length_C, Length_D, Length_E, Length_F, Length_G, Length_H,
  Length_I, Length_L, Length_M, Length_N, Length_P, Length_Q,
  Length_R, Length_S, Length_T, Length_U, Length_W, Length_Z)
speed_profile_sets_total <- c(current = NA, previous = NA)

if ((!is.null(state_codes)) && (!is.na(state_codes)) && (country_code3 == "usa" || country_code3 == "can")) {
    # US/CAN Display and Classic Field/Values

    airports_total <- c(current = NA, previous = NA)
    cities_total <- c(current = NA, previous = NA)
    expressways_total <- c(current = NA, previous = NA)
    ferries_total <- c(current = NA, previous = NA)
    gazetteer1_total <- c(current = NA, previous = NA)
    gazetteer2_total <- c(current = NA, previous = NA)
    gazetteer3_total <- c(current = NA, previous = NA)
    gazetteer4_total <- c(current = NA, previous = NA)
    gazetteer5_total <- c(current = NA, previous = NA)
    gazetteer6_total <- c(current = NA, previous = NA)
    gazetteer7_total <- c(current = NA, previous = NA)
    crossroads_total <- c(current = NA, previous = NA)
    geocode1_total <- c(current = NA, previous = NA)
    geocode2_total <- c(current = NA, previous = NA)
    sa_total <- c(current = NA, previous = NA)
    landmarks_total <- c(current = NA, previous = NA)
    landuse_total <- c(current = NA, previous = NA)
    localhwys_med_total <- c(current = NA, previous = NA)
    localrtes_total <- c(current = NA, previous = NA)
    oneways_total <- c(current = NA, previous = NA)
    primaryhwys_total <- c(current = NA, previous = NA)
    primaryhwys_med_total <- c(current = NA, previous = NA)
    railroads_total <- c(current = NA, previous = NA)
    regionalhwys_total <- c(current = NA, previous = NA)
    rivers_total <- c(current = NA, previous = NA)
    secondaryhwys_total <- c(current = NA, previous = NA)
    secondaryhwys_med_total <- c(current = NA, previous = NA)
    signposts_total <- c(current = NA, previous = NA)
    displaystreets_total <- c(current = NA, previous = NA)
    urbanareas_total <- c(current = NA, previous = NA)
    waterbodies_total <- c(current = NA, previous = NA)

    Length_displaystreets <- c(current = NA, previous = NA)
    Length_expressways <- c(current = NA, previous = NA)
    Length_localrtes <- c(current = NA, previous = NA)
    Length_primaryhwys <- c(current = NA, previous = NA)
    Length_secondaryhwys <- c(current = NA, previous = NA)

}

if (!is.null(country_code3[[1]]) && !is.na(country_code3) && (country_code3 == "usa")) {
    # US only

    counties_total <- c(current = NA, previous = NA)
    shldco_0to5_total <- c(current = NA, previous = NA)
    shldco_15to50_total <- c(current = NA, previous = NA)
    shldco_5to15_total <- c(current = NA, previous = NA)
    shldinter_0to5_total <- c(current = NA, previous = NA)
    shldinter_15to50_total <- c(current = NA, previous = NA)
    shldinter_5to15_total <- c(current = NA, previous = NA)
    shldstate_0to5_total <- c(current = NA, previous = NA)
    shldstate_15to50_total <- c(current = NA, previous = NA)
    shldstate_5to15_total <- c(current = NA, previous = NA)
    shldus_0to5_total <- c(current = NA, previous = NA)
    shldus_15to50_total <- c(current = NA, previous = NA)
    shldus_5to15_total <- c(current = NA, previous = NA)
    towns_total <- c(current = NA, previous = NA)
}

if (!is.null(country_code3[[1]]) && !is.na(country_code3) && (country_code3 == "can")) {
    # CAN only

    censusdivisions_total <- c(current = NA, previous = NA)
    censussubdivisions_total <- c(current = NA, previous = NA)
    shldprhwy_0to5_total <- c(current = NA, previous = NA)
    shldprhwy_15to50_total <- c(current = NA, previous = NA)
    shldprhwy_5to15_total <- c(current = NA, previous = NA)
    shldtchwy_0to5_total <- c(current = NA, previous = NA)
    shldtchwy_15to50_total <- c(current = NA, previous = NA)
    shldtchwy_5to15_total <- c(current = NA, previous = NA)
}

if (!is.null(country_code4[[1]]) && !is.na(country_code4)) {
    # EMEA Display and Classic Field/Values
    a0_total <- c(current = NA, previous = NA)
    a1_total <- c(current = NA, previous = NA)
    a2_total <- c(current = NA, previous = NA)
    a3_total <- c(current = NA, previous = NA)
    a4_total <- c(current = NA, previous = NA)
    a5_total <- c(current = NA, previous = NA)
    aa_total <- c(current = NA, previous = NA)
    ap_total <- c(current = NA, previous = NA)
    b_total <- c(current = NA, previous = NA)
    cr_total <- c(current = NA, previous = NA)
    sa_total <- c(current = NA, previous = NA)
    x1_total <- c(current = NA, previous = NA)
    x2_total <- c(current = NA, previous = NA)
    f_total <- c(current = NA, previous = NA)
    fi_total <- c(current = NA, previous = NA)
    fr_total <- c(current = NA, previous = NA)
    g1_total <- c(current = NA, previous = NA)
    g2_total <- c(current = NA, previous = NA)
    g3_total <- c(current = NA, previous = NA)
    g4_total <- c(current = NA, previous = NA)
    g5_total <- c(current = NA, previous = NA)
    g6_total <- c(current = NA, previous = NA)
    gf_total <- c(current = NA, previous = NA)
    lc_total <- c(current = NA, previous = NA)
    lu_total <- c(current = NA, previous = NA)
    nf_total <- c(current = NA, previous = NA)
    ow_total <- c(current = NA, previous = NA)
    p_total <- c(current = NA, previous = NA)
    pc_total <- c(current = NA, previous = NA)
    pci_total <- c(current = NA, previous = NA)
    pcp_total <- c(current = NA, previous = NA)
    pu_total <- c(current = NA, previous = NA)
    r_total <- c(current = NA, previous = NA)
    re_total <- c(current = NA, previous = NA)
    s1_total <- c(current = NA, previous = NA)
    s2_total <- c(current = NA, previous = NA)
    s3_total <- c(current = NA, previous = NA)
    s4_total <- c(current = NA, previous = NA)
    s5_total <- c(current = NA, previous = NA)
    s6_total <- c(current = NA, previous = NA)
    st_total <- c(current = NA, previous = NA)
    u_total <- c(current = NA, previous = NA)
    wm_total <- c(current = NA, previous = NA)
    w_total <- c(current = NA, previous = NA)
    zl_total <- c(current = NA, previous = NA)
    zp_total <- c(current = NA, previous = NA)

    Length_s1 <- c(current = NA, previous = NA)
    Length_s2 <- c(current = NA, previous = NA)
    Length_s3 <- c(current = NA, previous = NA)
    Length_s4 <- c(current = NA, previous = NA)
    Length_s5 <- c(current = NA, previous = NA)
    Length_s6 <- c(current = NA, previous = NA)
}

getNavStreetsValues <- function (ctx, valueList) {
    streets_context <- switch(
      ctx,
      "current" = streets_current,
      "previous" = streets_previous
    )

    valueIndex <- 0

    for (c in list(streets_total,
                   STREET, STREET2, STREET3, STREET4,
                   FROMLEFT, TOLEFT, FROMRIGHT, TORIGHT,
                   L_STRUCT_1, L_STRUCT_2, L_STRUCT_3,
                   L_STRUCT_4, L_STRUCT_5, L_STRUCT_6,
                   R_STRUCT_1, R_STRUCT_2, R_STRUCT_3,
                   R_STRUCT_4, R_STRUCT_5, R_STRUCT_6,
                   A1_LEFT, A1_RIGHT, LOCALITY_LEFT, LOCALITY_CODE_LEFT,
                   LOCALITY_RIGHT, LOCALITY_CODE_RIGHT,
                   PC_LEFT, PNAM_LEFT, PC_RIGHT, PNAM_RIGHT,
                   AREA_TYPE_0, AREA_TYPE_1, ROAD_TYPE_D, ROAD_TYPE_S,
                   SPEED, SPEED_VERIFIED, SPEED_AMPEAK, SPEED_PMPEAK,
                   SPEED_INTERPEAK, SPEED_NIGHT, SPEED_SEVENDAY,
                   ROUGHRD, SURFACE_TYPE_0, SURFACE_TYPE_1, SURFACE_TYPE_2,
                   SURFACE_TYPE_3, MAX_HEIGHT, MAX_WIDTH, MAX_WEIGHT,
                   ONEWAY_1, ONEWAY_2, ONEWAY_3, ONEWAY_4, ONEWAY_5, ONEWAY_6,
                   ROUTE_NUM, TOLL_false, TOLL_true, Length_total, Length_A,
                   Length_C, Length_D, Length_E, Length_F, Length_G, Length_H,
                   Length_I, Length_L, Length_M, Length_N, Length_P, Length_Q,
                   Length_R, Length_S, Length_T, Length_U, Length_W, Length_Z)
    ) {
        valueIndex <- (valueIndex + 1)
        #print(paste0(valueIndex, ": ", valueList[valueIndex]))
        c <- valueList[valueIndex]
    }

    for (c in c(1:length(streets_context[, 1]))) {
        attr <- streets_context[c, 1]
        value <- streets_context[c, 2]
        if (!is.null(attr) && !is.na(attr)) {
            if (all(state_flag) && length(state_codes) > 0) {
                sp_pattern <- paste0("(?:", country_code3, "_", tolower(state_codes[1]), "_|", country_code4, "|\\s|SPEED_|MAX_|\\()(?:ROAD_CLASS\\:\\s)?([\\w]+\\)?)")
            } else {
                sp_pattern <- paste0("(?:", country_code3, "_|", country_code4, "|\\s|SPEED_|MAX_|\\()(?:ROAD_CLASS\\:\\s)?([\\w]+\\)?)")
            }
            attr_match <- str_match(attr, sp_pattern)[,2]
            # print(attr_match)

            if (is.na(attr_match)) {
                print(paste0("If attr match is NA, "))
                switch(
                    attr,
                    "SPEED" = {
                        print(paste0("then attr is 'SPEED': ", value))
                        #attr_match <- c("SPEED")
                        SPEED[ctx] <- value
                    },
                    "STREET" = {
                        print(paste0("then attr is 'STREET': ", value))
                        #attr_match <- c("STREET")
                        STREET[ctx] <- value
                    },
                    "STREET2" = {
                        print(paste0("then attr is 'STREET2': ", value))
                        #attr_match <- c("STREET2")
                        STREET2[ctx] <- value
                    },
                    "STREET3" = {
                        print(paste0("then attr is 'STREET3': ", value))
                        #attr_match <- c("STREET3")
                        STREET3[ctx] <- value
                    },
                    "STREET4" = {
                        print(paste0("then attr is 'STREET4': ", value))
                        #attr_match <- c("STREET4")
                        STREET4[ctx] <- value
                    },
                    "FROMLEFT" = {
                        print(paste0("then attr is 'FROMLEFT': ", value))
                        FROMLEFT[ctx] <- value
                    },
                    "TOLEFT" = {
                        print(paste0("then attr is 'TOLEFT': ", value))
                        TOLEFT[ctx] <- value
                    },
                    "FROMRIGHT" = {
                        print(paste0("then attr is 'FROMRIGHT': ", value))
                        FROMRIGHT[ctx] <- value
                    },
                    "TORIGHT" = {
                        print(paste0("then attr is 'TORIGHT': ", value))
                        TORIGHT[ctx] <- value
                    },
                    "A1_LEFT" = {
                        print(paste0("then attr is 'A1_LEFT': ", value))
                        A1_LEFT[ctx] <- value
                    },
                    "A1_RIGHT" = {
                        print(paste0("then attr is 'A1_RIGHT': ", value))
                        A1_RIGHT[ctx] <- value
                    },
                    "LOCALITY_LEFT" = {
                        print(paste0("then attr is 'LOCALITY_LEFT': ", value))
                        LOCALITY_LEFT[ctx] <- value
                    },
                    "LOCALITY_CODE_LEFT" = {
                        print(paste0("then attr is 'LOCALITY_CODE_LEFT': ", value))
                        LOCALITY_CODE_LEFT[ctx] <- value
                    },
                    "LOCALITY_RIGHT" = {
                        print(paste0("then attr is 'LOCALITY_RIGHT': ", value))
                        LOCALITY_RIGHT[ctx] <- value
                    },
                    "LOCALITY_CODE_RIGHT" = {
                        print(paste0("then attr is 'LOCALITY_CODE_RIGHT': ", value))
                        LOCALITY_CODE_RIGHT[ctx] <- value
                    },
                    "PC_LEFT" = {
                        print(paste0("then attr is 'PC_LEFT': ", value))
                        PC_LEFT[ctx] <- value
                    },
                    "PNAM_LEFT" = {
                        print(paste0("then attr is 'PNAM_LEFT': ", value))
                        PNAM_LEFT[ctx] <- value
                    },
                    "PC_RIGHT" = {
                        print(paste0("then attr is 'PC_RIGHT': ", value))
                        PC_RIGHT[ctx] <- value
                    },
                    "PNAM_RIGHT" = {
                        print(paste0("then attr is 'PNAM_RIGHT': ", value))
                        PNAM_RIGHT[ctx] <- value
                    },
                    "ROUTE_NUM" = {
                        print(paste0("then attr is 'ROUTE_NUM': ", value))
                        ROUTE_NUM[ctx] <- value
                    },
                    "ROUGHRD" = {
                        print(paste0("then attr is 'ROUGHRD': ", value))
                        ROUGHRD[ctx] <- value
                    }
                )
            }

            switch(
                attr_match,
                "streets" = { streets_total[ctx] <- value },
                "VERIFIED" = { SPEED_VERIFIED[ctx] <- value },
                "AMPEAK" = { SPEED_AMPEAK[ctx] <- value },
                "PMPEAK" = { SPEED_PMPEAK[ctx] <- value },
                "INTERPEAK" = { SPEED_INTERPEAK[ctx] <- value },
                "NIGHT" = { SPEED_NIGHT[ctx] <- value },
                "SEVENDAY" = { SPEED_SEVENDAY[ctx] <- value },
                "HEIGHT" = { MAX_HEIGHT[ctx] <- value },
                "WIDTH" = { MAX_WIDTH[ctx] <- value },
                "WEIGHT" = { MAX_WEIGHT[ctx] <- value },
                "0" = {
                    switch(
                        attr,
                        "AREA_TYPE: 0" = { AREA_TYPE_0[ctx] <- value },
                        "SURFACE_TYPE: 0" = { SURFACE_TYPE_0[ctx] <- value }
                    )
                },
                "1" = {
                    switch(
                        attr,
                        "AREA_TYPE: 1" = { AREA_TYPE_1[ctx] <- value },
                        "L_STRUCT: 1" = { L_STRUCT_1[ctx] <- value },
                        "R_STRUCT: 1" = { R_STRUCT_1[ctx] <- value },
                        "ONEWAY: 1" = { ONEWAY_1[ctx] <- value },
                        "SURFACE_TYPE: 1" = { SURFACE_TYPE_1[ctx] <- value }
                    )
                },
                "2" = {
                    switch(
                        attr,
                        "L_STRUCT: 2" = { L_STRUCT_2[ctx] <- value },
                        "R_STRUCT: 2" = { R_STRUCT_2[ctx] <- value },
                        "ONEWAY: 2" = { ONEWAY_2[ctx] <- value },
                        "SURFACE_TYPE: 2" = { SURFACE_TYPE_2[ctx] <- value }
                    )
                },
                "3" = {
                    switch(
                        attr,
                        "L_STRUCT: 3" = { L_STRUCT_3[ctx] <- value },
                        "R_STRUCT: 3" = { R_STRUCT_3[ctx] <- value },
                        "ONEWAY: 3" = { ONEWAY_3[ctx] <- value },
                        "SURFACE_TYPE: 3" = { SURFACE_TYPE_3[ctx] <- value }
                    )
                },
                "4" = {
                    switch(
                        attr,
                        "L_STRUCT: 4" = { L_STRUCT_4[ctx] <- value },
                        "R_STRUCT: 4" = { R_STRUCT_4[ctx] <- value },
                        "ONEWAY: 4" = { ONEWAY_4[ctx] <- value }
                    )
                },
                "5" = {
                    switch(
                        attr,
                        "L_STRUCT: 5" = { L_STRUCT_5[ctx] <- value },
                        "R_STRUCT: 5" = { R_STRUCT_5[ctx] <- value },
                        "ONEWAY: 5" = { ONEWAY_5[ctx] <- value }
                    )
                },
                "6" = {
                    switch(
                        attr,
                        "L_STRUCT: 6" = { L_STRUCT_6[ctx] <- value },
                        "R_STRUCT: 6" = { R_STRUCT_6[ctx] <- value },
                        "ONEWAY: 6" = { ONEWAY_6[ctx] <- value }
                    )
                },
                "D" = { ROAD_TYPE_D[ctx] <- value },   # ROAD_TYPE: D
                "S" = { ROAD_TYPE_S[ctx] <- value },   # ROAD_TYPE: S
                "false" = { TOLL_false[ctx] <- value },   # TOLL: false
                "true" = { TOLL_true[ctx] <- value },     # TOLL: true
                "total)" = { Length_total[ctx] <- value }, # Length: ...
                "A)" = { Length_A[ctx] <- value },
                "C)" = { Length_C[ctx] <- value },
                "D)" = { Length_D[ctx] <- value },
                "E)" = { Length_E[ctx] <- value },
                "F)" = { Length_F[ctx] <- value },
                "G)" = { Length_G[ctx] <- value },
                "H)" = { Length_H[ctx] <- value },
                "I)" = { Length_I[ctx] <- value },
                "L)" = { Length_L[ctx] <- value },
                "M)" = { Length_M[ctx] <- value },
                "N)" = { Length_N[ctx] <- value },
                "P)" = { Length_P[ctx] <- value },
                "Q)" = { Length_Q[ctx] <- value },
                "R)" = { Length_R[ctx] <- value },
                "S)" = { Length_S[ctx] <- value },
                "T)" = { Length_T[ctx] <- value },
                "U)" = { Length_U[ctx] <- value },
                "W)" = { Length_W[ctx] <- value },
                "Z)" = { Length_Z[ctx] <- value }
            )
        }
    }

    list(streets_total,
         STREET, STREET2, STREET3, STREET4,
         FROMLEFT, TOLEFT, FROMRIGHT, TORIGHT,
         L_STRUCT_1, L_STRUCT_2, L_STRUCT_3,
         L_STRUCT_4, L_STRUCT_5, L_STRUCT_6,
         R_STRUCT_1, R_STRUCT_2, R_STRUCT_3,
         R_STRUCT_4, R_STRUCT_5, R_STRUCT_6,
         A1_LEFT, A1_RIGHT, LOCALITY_LEFT, LOCALITY_CODE_LEFT,
         LOCALITY_RIGHT, LOCALITY_CODE_RIGHT,
         PC_LEFT, PNAM_LEFT, PC_RIGHT, PNAM_RIGHT,
         AREA_TYPE_0, AREA_TYPE_1, ROAD_TYPE_D, ROAD_TYPE_S,
         SPEED, SPEED_VERIFIED, SPEED_AMPEAK, SPEED_PMPEAK,
         SPEED_INTERPEAK, SPEED_NIGHT, SPEED_SEVENDAY,
         ROUGHRD, SURFACE_TYPE_0, SURFACE_TYPE_1, SURFACE_TYPE_2,
         SURFACE_TYPE_3, MAX_HEIGHT, MAX_WIDTH, MAX_WEIGHT,
         ONEWAY_1, ONEWAY_2, ONEWAY_3, ONEWAY_4, ONEWAY_5, ONEWAY_6,
         ROUTE_NUM, TOLL_false, TOLL_true, Length_total, Length_A,
         Length_C, Length_D, Length_E, Length_F, Length_G, Length_H,
         Length_I, Length_L, Length_M, Length_N, Length_P, Length_Q,
         Length_R, Length_S, Length_T, Length_U, Length_W, Length_Z)
}

getStatValue <- function (stat_frame, layer_name) {
    value <- c(NA)
    for (c in c(1:length(stat_frame[, 1]))) {
        attr <- stat_frame[c, 1]
        if (!is.null(attr) && !is.na(attr) && grepl(layer_name, attr)) {
            value <- stat_frame[c, 2]
        }
    }
    value
}

getStatLength <- function (stat_frame) {
    stat_length <- c(NA)
    for (c in c(1:length(stat_frame[, 1]))) {
        attr <- stat_frame[c, 1]
        value <- stat_frame[c, 2]
        if (!is.null(attr) && !is.na(attr)) {
            sp_pattern <- paste0("(?:", country_code3, "_|", country_code4, "|\\s|SPEED_|MAX_|\\()(?:ROAD_CLASS\\:\\s)?([\\w]+)\\)?")
            attr_match <- str_match(attr, sp_pattern)[,2]
            print(attr_match)

            if (!is.null(attr_match) && !is.na(attr_match) && attr_match == "total") {
                stat_length <- value
            }
        }
    }

    stat_length
}

print(length(streets_current[1]))
print(names(streets_current[1]))

# Get current counts
context <- "current"
nodes_total[context] <- getStatValue(nodes_current, "nodes")
restrictions_total[context] <- getStatValue(restrictions_current, "restrictions")
streetsValuesList <- getNavStreetsValues(context, streetsValuesList)
streets_total[context] <- streetsValuesList[[1]][context]
STREET[context] <- streetsValuesList[[2]][context]
STREET2[context] <- streetsValuesList[[3]][context]
STREET3[context] <- streetsValuesList[[4]][context]
STREET4[context] <- streetsValuesList[[5]][context]
FROMLEFT[context] <- streetsValuesList[[6]][context]
TOLEFT[context] <- streetsValuesList[[7]][context]
FROMRIGHT[context] <- streetsValuesList[[8]][context]
TORIGHT[context] <- streetsValuesList[[9]][context]
L_STRUCT_1[context] <- streetsValuesList[[10]][context]
L_STRUCT_2[context] <- streetsValuesList[[11]][context]
L_STRUCT_3[context] <- streetsValuesList[[12]][context]
L_STRUCT_4[context] <- streetsValuesList[[13]][context]
L_STRUCT_5[context] <- streetsValuesList[[14]][context]
L_STRUCT_6[context] <- streetsValuesList[[15]][context]
R_STRUCT_1[context] <- streetsValuesList[[16]][context]
R_STRUCT_2[context] <- streetsValuesList[[17]][context]
R_STRUCT_3[context] <- streetsValuesList[[18]][context]
R_STRUCT_4[context] <- streetsValuesList[[19]][context]
R_STRUCT_5[context] <- streetsValuesList[[20]][context]
R_STRUCT_6[context] <- streetsValuesList[[21]][context]
A1_LEFT[context] <- streetsValuesList[[22]][context]
A1_RIGHT[context] <- streetsValuesList[[23]][context]
LOCALITY_LEFT[context] <- streetsValuesList[[24]][context]
LOCALITY_CODE_LEFT[context] <- streetsValuesList[[25]][context]
LOCALITY_RIGHT[context] <- streetsValuesList[[26]][context]
LOCALITY_CODE_RIGHT[context] <- streetsValuesList[[27]][context]
PC_LEFT[context] <- streetsValuesList[[28]][context]
PNAM_LEFT[context] <- streetsValuesList[[29]][context]
PC_RIGHT[context] <- streetsValuesList[[30]][context]
PNAM_RIGHT[context] <- streetsValuesList[[31]][context]
AREA_TYPE_0[context] <- streetsValuesList[[32]][context]
AREA_TYPE_1[context] <- streetsValuesList[[33]][context]
ROAD_TYPE_D[context] <- streetsValuesList[[34]][context]
ROAD_TYPE_S[context] <- streetsValuesList[[35]][context]
SPEED[context] <- streetsValuesList[[36]][context]
SPEED_VERIFIED[context] <- streetsValuesList[[37]][context]
SPEED_AMPEAK[context] <- streetsValuesList[[38]][context]
SPEED_PMPEAK[context] <- streetsValuesList[[39]][context]
SPEED_INTERPEAK[context] <- streetsValuesList[[40]][context]
SPEED_NIGHT[context] <- streetsValuesList[[41]][context]
SPEED_SEVENDAY[context] <- streetsValuesList[[42]][context]
ROUGHRD[context] <- streetsValuesList[[43]][context]
SURFACE_TYPE_0[context] <- streetsValuesList[[44]][context]
SURFACE_TYPE_1[context] <- streetsValuesList[[45]][context]
SURFACE_TYPE_2[context] <- streetsValuesList[[46]][context]
SURFACE_TYPE_3[context] <- streetsValuesList[[47]][context]
MAX_HEIGHT[context] <- streetsValuesList[[48]][context]
MAX_WIDTH[context] <- streetsValuesList[[49]][context]
MAX_WEIGHT[context] <- streetsValuesList[[50]][context]
ONEWAY_1[context] <- streetsValuesList[[51]][context]
ONEWAY_2[context] <- streetsValuesList[[52]][context]
ONEWAY_3[context] <- streetsValuesList[[53]][context]
ONEWAY_4[context] <- streetsValuesList[[54]][context]
ONEWAY_5[context] <- streetsValuesList[[55]][context]
ONEWAY_6[context] <- streetsValuesList[[56]][context]
ROUTE_NUM[context] <- streetsValuesList[[57]][context]
TOLL_false[context] <- streetsValuesList[[58]][context]
TOLL_true[context] <- streetsValuesList[[59]][context]
Length_total[context] <- streetsValuesList[[60]][context]
Length_A[context] <- streetsValuesList[[61]][context]
Length_C[context] <- streetsValuesList[[62]][context]
Length_D[context] <- streetsValuesList[[63]][context]
Length_E[context] <- streetsValuesList[[64]][context]
Length_F[context] <- streetsValuesList[[65]][context]
Length_G[context] <- streetsValuesList[[66]][context]
Length_H[context] <- streetsValuesList[[67]][context]
Length_I[context] <- streetsValuesList[[68]][context]
Length_L[context] <- streetsValuesList[[69]][context]
Length_M[context] <- streetsValuesList[[70]][context]
Length_N[context] <- streetsValuesList[[71]][context]
Length_P[context] <- streetsValuesList[[72]][context]
Length_Q[context] <- streetsValuesList[[73]][context]
Length_R[context] <- streetsValuesList[[74]][context]
Length_S[context] <- streetsValuesList[[75]][context]
Length_T[context] <- streetsValuesList[[76]][context]
Length_U[context] <- streetsValuesList[[77]][context]
Length_W[context] <- streetsValuesList[[78]][context]
Length_Z[context] <- streetsValuesList[[79]][context]
speed_profile_sets_total[context] <- getStatValue(speed_profile_sets_current, "speed_profile_sets")

if ((!is.null(state_codes)) && (!is.na(state_codes)) && (country_code3 == "usa" || country_code3 == "can")) {
    airports_total[context] <- getStatValue(airports_current, "airports")
    cities_total[context] <- getStatValue(cities_current, "cities")
    expressways_total[context] <- getStatValue(expressways_current, "expressways")
    ferries_total[context] <- getStatValue(ferries_current, "ferries")
    gazetteer1_total[context] <- getStatValue(gazetteer1_current, "gazetteer1")
    gazetteer2_total[context] <- getStatValue(gazetteer2_current, "gazetteer2")
    gazetteer3_total[context] <- getStatValue(gazetteer3_current, "gazetteer3")
    gazetteer4_total[context] <- getStatValue(gazetteer4_current, "gazetteer4")
    gazetteer5_total[context] <- getStatValue(gazetteer5_current, "gazetteer5")
    gazetteer6_total[context] <- getStatValue(gazetteer6_current, "gazetteer6")
    gazetteer7_total[context] <- getStatValue(gazetteer7_current, "gazetteer7")
    crossroads_total[context] <- getStatValue(crossroads_current, "crossroads")
    geocode1_total[context] <- getStatValue(geocode1_current, "geocode1")
    geocode2_total[context] <- getStatValue(geocode2_current, "geocode2")
    sa_total[context] <- getStatValue(sa_current, "sa")
    landmarks_total[context] <- getStatValue(landmarks_current, "landmarks")
    landuse_total[context] <- getStatValue(landuse_current, "landuse")
    localhwys_med_total[context] <- getStatValue(localhwys_med_current, "localhwys_med")
    localrtes_total[context] <- getStatValue(localrtes_current, "localrtes")
    oneways_total[context] <- getStatValue(oneways_current, "oneways")
    primaryhwys_total[context] <- getStatValue(primaryhwys_current, "primaryhwys")
    primaryhwys_med_total[context] <- getStatValue(primaryhwys_med_current, "primaryhwys_med")
    railroads_total[context] <- getStatValue(railroads_current, "railroads")
    regionalhwys_total[context] <- getStatValue(regionalhwys_current, "regionalhwys")
    rivers_total[context] <- getStatValue(rivers_current, "rivers")
    secondaryhwys_total[context] <- getStatValue(secondaryhwys_current, "secondaryhwys")
    secondaryhwys_med_total[context] <- getStatValue(secondaryhwys_med_current, "secondaryhwys_med")
    signposts_total[context] <- getStatValue(signposts_current, "signposts")
    displaystreets_total[context] <- getStatValue(displaystreets_current, "streets")
    urbanareas_total[context] <- getStatValue(urbanareas_current, "urbanareas")
    waterbodies_total[context] <- getStatValue(waterbodies_current, "waterbodies")

    Length_displaystreets[context] <- getStatLength(displaystreets_current)
    Length_expressways[context] <- getStatLength(expressways_current)
    Length_localrtes[context] <- getStatLength(localrtes_current)
    Length_primaryhwys[context] <- getStatLength(primaryhwys_current)
    Length_secondaryhwys[context] <- getStatLength(secondaryhwys_current)

}

if (!is.null(country_code3[[1]]) && !is.na(country_code3) && (country_code3 == "usa")) {
    # US only

    counties_total[context] <- getStatValue(counties_current, "counties")
    shldco_0to5_total[context] <- getStatValue(shldco_0to5_current, "shldco_0to5")
    shldco_5to15_total[context] <- getStatValue(shldco_5to15_current, "shldco_5to15")
    shldco_15to50_total[context] <- getStatValue(shldco_15to50_current, "shldco_15to50")
    shldinter_0to5_total[context] <- getStatValue(shldinter_0to5_current, "shldinter_0to5")
    shldinter_5to15_total[context] <- getStatValue(shldinter_5to15_current, "shldinter_5to15")
    shldinter_15to50_total[context] <- getStatValue(shldinter_15to50_current, "shldinter_15to50")
    shldstate_0to5_total[context] <- getStatValue(shldstate_0to5_current, "shldstate_0to5")
    shldstate_5to15_total[context] <- getStatValue(shldstate_5to15_current, "shldstate_5to15")
    shldstate_15to50_total[context] <- getStatValue(shldstate_15to50_current, "shldstate_15to50")
    shldus_0to5_total[context] <- getStatValue(shldus_0to5_current, "shldus_0to5")
    shldus_5to15_total[context] <- getStatValue(shldus_5to15_current, "shldus_5to15")
    shldus_15to50_total[context] <- getStatValue(shldus_15to50_current, "shldus_15to50")
    towns_total[context] <- getStatValue(towns_current, "towns")
}

if (!is.null(country_code3[[1]]) && !is.na(country_code3) && (country_code3 == "can")) {
    # CAN only

    censusdivisions_total[context] <- getStatValue(censusdivisions_current, "censusdivisions")
    censussubdivisions_total[context] <- getStatValue(censussubdivisions_current, "censussubdivisions")
    shldprhwy_0to5_total[context] <- getStatValue(shldprhwy_0to5_current, "shldprhwy_0to5")
    shldprhwy_5to15_total[context] <- getStatValue(shldprhwy_5to15_current, "shldprhwy_5to15")
    shldprhwy_15to50_total[context] <- getStatValue(shldprhwy_15to50_current, "shldprhwy_15to50")
    shldtchwy_0to5_total[context] <- getStatValue(shldtchwy_0to5_current, "shldtchwy_0to5")
    shldtchwy_5to15_total[context] <- getStatValue(shldtchwy_5to15_current, "shldtchwy_5to15")
    shldtchwy_15to50_total[context] <- getStatValue(shldtchwy_15to50_current, "shldtchwy_15to50")
}

if (!is.null(country_code4[[1]]) && !is.na(country_code4)) {
    a0_total[context] <- getStatValue(a0_current, "a0")
    a1_total[context] <- getStatValue(a1_current, "a1")
    a2_total[context] <- getStatValue(a2_current, "a2")
    a3_total[context] <- getStatValue(a3_current, "a3")
    a4_total[context] <- getStatValue(a4_current, "a4")
    a5_total[context] <- getStatValue(a5_current, "a5")
    aa_total[context] <- getStatValue(aa_current, "aa")
    ap_total[context] <- getStatValue(ap_current, "ap")
    b_total[context] <- getStatValue(b_current, "b")
    cr_total[context] <- getStatValue(cr_current, "cr")
    sa_total[context] <- getStatValue(sa_current, "sa")
    x1_total[context] <- getStatValue(x1_current, "x1")
    x2_total[context] <- getStatValue(x2_current, "x2")
    f_total[context] <- getStatValue(f_current, "f")
    fi_total[context] <- getStatValue(fi_current, "fi")
    fr_total[context] <- getStatValue(fr_current, "fr")
    g1_total[context] <- getStatValue(g1_current, "g1")
    g2_total[context] <- getStatValue(g2_current, "g2")
    g3_total[context] <- getStatValue(g3_current, "g3")
    g4_total[context] <- getStatValue(g4_current, "g4")
    g5_total[context] <- getStatValue(g5_current, "g5")
    g6_total[context] <- getStatValue(g6_current, "g6")
    gf_total[context] <- getStatValue(gf_current, "gf")
    lc_total[context] <- getStatValue(lc_current, "lc")
    lu_total[context] <- getStatValue(lu_current, "lu")
    nf_total[context] <- getStatValue(nf_current, "nf")
    ow_total[context] <- getStatValue(ow_current, "ow")
    p_total[context] <- getStatValue(p_current, "p")
    pc_total[context] <- getStatValue(pc_current, "pc")
    pci_total[context] <- getStatValue(pci_current, "pci")
    pcp_total[context] <- getStatValue(pcp_current, "pcp")
    pu_total[context] <- getStatValue(pu_current, "pu")
    r_total[context] <- getStatValue(r_current, "r")
    re_total[context] <- getStatValue(re_current, "re")
    s1_total[context] <- getStatValue(s1_current, "s1")
    s2_total[context] <- getStatValue(s2_current, "s2")
    s3_total[context] <- getStatValue(s3_current, "s3")
    s4_total[context] <- getStatValue(s4_current, "s4")
    s5_total[context] <- getStatValue(s5_current, "s5")
    s6_total[context] <- getStatValue(s6_current, "s6")
    st_total[context] <- getStatValue(st_current, "st")
    u_total[context] <- getStatValue(u_current, "u")
    wm_total[context] <- getStatValue(wm_current, "wm")
    w_total[context] <- getStatValue(w_current, "w")
    zl_total[context] <- getStatValue(zl_current, "zl")
    zp_total[context] <- getStatValue(zp_current, "zp")

    Length_s1[context] <- getStatLength(s1_current)
    Length_s2[context] <- getStatLength(s2_current)
    Length_s3[context] <- getStatLength(s3_current)
    Length_s4[context] <- getStatLength(s4_current)
    Length_s5[context] <- getStatLength(s5_current)
    Length_s6[context] <- getStatLength(s6_current)
}

print(length(streets_previous[1]))
print(names(streets_previous[1]))

# Get previous counts
context <- "previous"
nodes_total[context] <- getStatValue(nodes_previous, "nodes")
restrictions_total[context] <- getStatValue(restrictions_previous, "restrictions")
streetsValuesList <- getNavStreetsValues(context, streetsValuesList)
streets_total[context] <- streetsValuesList[[1]][context]
STREET[context] <- streetsValuesList[[2]][context]
STREET2[context] <- streetsValuesList[[3]][context]
STREET3[context] <- streetsValuesList[[4]][context]
STREET4[context] <- streetsValuesList[[5]][context]
FROMLEFT[context] <- streetsValuesList[[6]][context]
TOLEFT[context] <- streetsValuesList[[7]][context]
FROMRIGHT[context] <- streetsValuesList[[8]][context]
TORIGHT[context] <- streetsValuesList[[9]][context]
L_STRUCT_1[context] <- streetsValuesList[[10]][context]
L_STRUCT_2[context] <- streetsValuesList[[11]][context]
L_STRUCT_3[context] <- streetsValuesList[[12]][context]
L_STRUCT_4[context] <- streetsValuesList[[13]][context]
L_STRUCT_5[context] <- streetsValuesList[[14]][context]
L_STRUCT_6[context] <- streetsValuesList[[15]][context]
R_STRUCT_1[context] <- streetsValuesList[[16]][context]
R_STRUCT_2[context] <- streetsValuesList[[17]][context]
R_STRUCT_3[context] <- streetsValuesList[[18]][context]
R_STRUCT_4[context] <- streetsValuesList[[19]][context]
R_STRUCT_5[context] <- streetsValuesList[[20]][context]
R_STRUCT_6[context] <- streetsValuesList[[21]][context]
A1_LEFT[context] <- streetsValuesList[[22]][context]
A1_RIGHT[context] <- streetsValuesList[[23]][context]
LOCALITY_LEFT[context] <- streetsValuesList[[24]][context]
LOCALITY_CODE_LEFT[context] <- streetsValuesList[[25]][context]
LOCALITY_RIGHT[context] <- streetsValuesList[[26]][context]
LOCALITY_CODE_RIGHT[context] <- streetsValuesList[[27]][context]
PC_LEFT[context] <- streetsValuesList[[28]][context]
PNAM_LEFT[context] <- streetsValuesList[[29]][context]
PC_RIGHT[context] <- streetsValuesList[[30]][context]
PNAM_RIGHT[context] <- streetsValuesList[[31]][context]
AREA_TYPE_0[context] <- streetsValuesList[[32]][context]
AREA_TYPE_1[context] <- streetsValuesList[[33]][context]
ROAD_TYPE_D[context] <- streetsValuesList[[34]][context]
ROAD_TYPE_S[context] <- streetsValuesList[[35]][context]
SPEED[context] <- streetsValuesList[[36]][context]
SPEED_VERIFIED[context] <- streetsValuesList[[37]][context]
SPEED_AMPEAK[context] <- streetsValuesList[[38]][context]
SPEED_PMPEAK[context] <- streetsValuesList[[39]][context]
SPEED_INTERPEAK[context] <- streetsValuesList[[40]][context]
SPEED_NIGHT[context] <- streetsValuesList[[41]][context]
SPEED_SEVENDAY[context] <- streetsValuesList[[42]][context]
ROUGHRD[context] <- streetsValuesList[[43]][context]
SURFACE_TYPE_0[context] <- streetsValuesList[[44]][context]
SURFACE_TYPE_1[context] <- streetsValuesList[[45]][context]
SURFACE_TYPE_2[context] <- streetsValuesList[[46]][context]
SURFACE_TYPE_3[context] <- streetsValuesList[[47]][context]
MAX_HEIGHT[context] <- streetsValuesList[[48]][context]
MAX_WIDTH[context] <- streetsValuesList[[49]][context]
MAX_WEIGHT[context] <- streetsValuesList[[50]][context]
ONEWAY_1[context] <- streetsValuesList[[51]][context]
ONEWAY_2[context] <- streetsValuesList[[52]][context]
ONEWAY_3[context] <- streetsValuesList[[53]][context]
ONEWAY_4[context] <- streetsValuesList[[54]][context]
ONEWAY_5[context] <- streetsValuesList[[55]][context]
ONEWAY_6[context] <- streetsValuesList[[56]][context]
ROUTE_NUM[context] <- streetsValuesList[[57]][context]
TOLL_false[context] <- streetsValuesList[[58]][context]
TOLL_true[context] <- streetsValuesList[[59]][context]
Length_total[context] <- streetsValuesList[[60]][context]
Length_A[context] <- streetsValuesList[[61]][context]
Length_C[context] <- streetsValuesList[[62]][context]
Length_D[context] <- streetsValuesList[[63]][context]
Length_E[context] <- streetsValuesList[[64]][context]
Length_F[context] <- streetsValuesList[[65]][context]
Length_G[context] <- streetsValuesList[[66]][context]
Length_H[context] <- streetsValuesList[[67]][context]
Length_I[context] <- streetsValuesList[[68]][context]
Length_L[context] <- streetsValuesList[[69]][context]
Length_M[context] <- streetsValuesList[[70]][context]
Length_N[context] <- streetsValuesList[[71]][context]
Length_P[context] <- streetsValuesList[[72]][context]
Length_Q[context] <- streetsValuesList[[73]][context]
Length_R[context] <- streetsValuesList[[74]][context]
Length_S[context] <- streetsValuesList[[75]][context]
Length_T[context] <- streetsValuesList[[76]][context]
Length_U[context] <- streetsValuesList[[77]][context]
Length_W[context] <- streetsValuesList[[78]][context]
Length_Z[context] <- streetsValuesList[[79]][context]
speed_profile_sets_total[context] <- getStatValue(speed_profile_sets_previous, "speed_profile_sets")

if ((!is.null(state_codes)) && (!is.na(state_codes)) && (country_code3 == "usa" || country_code3 == "can")) {
    airports_total[context] <- getStatValue(airports_previous, "airports")
    cities_total[context] <- getStatValue(cities_previous, "cities")
    expressways_total[context] <- getStatValue(expressways_previous, "expressways")
    ferries_total[context] <- getStatValue(ferries_previous, "ferries")
    gazetteer1_total[context] <- getStatValue(gazetteer1_previous, "gazetteer1")
    gazetteer2_total[context] <- getStatValue(gazetteer2_previous, "gazetteer2")
    gazetteer3_total[context] <- getStatValue(gazetteer3_previous, "gazetteer3")
    gazetteer4_total[context] <- getStatValue(gazetteer4_previous, "gazetteer4")
    gazetteer5_total[context] <- getStatValue(gazetteer5_previous, "gazetteer5")
    gazetteer6_total[context] <- getStatValue(gazetteer6_previous, "gazetteer6")
    gazetteer7_total[context] <- getStatValue(gazetteer7_previous, "gazetteer7")
    crossroads_total[context] <- getStatValue(crossroads_previous, "crossroads")
    geocode1_total[context] <- getStatValue(geocode1_previous, "geocode1")
    geocode2_total[context] <- getStatValue(geocode2_previous, "geocode2")
    sa_total[context] <- getStatValue(sa_previous, "sa")
    landmarks_total[context] <- getStatValue(landmarks_previous, "landmarks")
    landuse_total[context] <- getStatValue(landuse_previous, "landuse")
    localhwys_med_total[context] <- getStatValue(localhwys_med_previous, "localhwys_med")
    localrtes_total[context] <- getStatValue(localrtes_previous, "localrtes")
    oneways_total[context] <- getStatValue(oneways_previous, "oneways")
    primaryhwys_total[context] <- getStatValue(primaryhwys_previous, "primaryhwys")
    primaryhwys_med_total[context] <- getStatValue(primaryhwys_med_previous, "primaryhwys_med")
    railroads_total[context] <- getStatValue(railroads_previous, "railroads")
    regionalhwys_total[context] <- getStatValue(regionalhwys_previous, "regionalhwys")
    rivers_total[context] <- getStatValue(rivers_previous, "rivers")
    secondaryhwys_total[context] <- getStatValue(secondaryhwys_previous, "secondaryhwys")
    secondaryhwys_med_total[context] <- getStatValue(secondaryhwys_med_previous, "secondaryhwys_med")
    signposts_total[context] <- getStatValue(signposts_previous, "signposts")
    displaystreets_total[context] <- getStatValue(displaystreets_previous, "streets")
    urbanareas_total[context] <- getStatValue(urbanareas_previous, "urbanareas")
    waterbodies_total[context] <- getStatValue(waterbodies_previous, "waterbodies")

    Length_displaystreets[context] <- getStatLength(displaystreets_previous)
    Length_expressways[context] <- getStatLength(expressways_previous)
    Length_localrtes[context] <- getStatLength(localrtes_previous)
    Length_primaryhwys[context] <- getStatLength(primaryhwys_previous)
    Length_secondaryhwys[context] <- getStatLength(secondaryhwys_previous)

}

if (!is.null(country_code3[[1]]) && !is.na(country_code3) && (country_code3 == "usa")) {
    # US only

    counties_total[context] <- getStatValue(counties_previous, "counties")
    shldco_0to5_total[context] <- getStatValue(shldco_0to5_previous, "shldco_0to5")
    shldco_5to15_total[context] <- getStatValue(shldco_5to15_previous, "shldco_5to15")
    shldco_15to50_total[context] <- getStatValue(shldco_15to50_previous, "shldco_15to50")
    shldinter_0to5_total[context] <- getStatValue(shldinter_0to5_previous, "shldinter_0to5")
    shldinter_5to15_total[context] <- getStatValue(shldinter_5to15_previous, "shldinter_5to15")
    shldinter_15to50_total[context] <- getStatValue(shldinter_15to50_previous, "shldinter_15to50")
    shldstate_0to5_total[context] <- getStatValue(shldstate_0to5_previous, "shldstate_0to5")
    shldstate_5to15_total[context] <- getStatValue(shldstate_5to15_previous, "shldstate_5to15")
    shldstate_15to50_total[context] <- getStatValue(shldstate_15to50_previous, "shldstate_15to50")
    shldus_0to5_total[context] <- getStatValue(shldus_0to5_previous, "shldus_0to5")
    shldus_5to15_total[context] <- getStatValue(shldus_5to15_previous, "shldus_5to15")
    shldus_15to50_total[context] <- getStatValue(shldus_15to50_previous, "shldus_15to50")
    towns_total[context] <- getStatValue(towns_previous, "towns")
}

if (!is.null(country_code3[[1]]) && !is.na(country_code3) && (country_code3 == "can")) {
    # CAN only

    censusdivisions_total[context] <- getStatValue(censusdivisions_previous, "censusdivisions")
    censussubdivisions_total[context] <- getStatValue(censussubdivisions_previous, "censussubdivisions")
    shldprhwy_0to5_total[context] <- getStatValue(shldprhwy_0to5_previous, "shldprhwy_0to5")
    shldprhwy_5to15_total[context] <- getStatValue(shldprhwy_5to15_previous, "shldprhwy_5to15")
    shldprhwy_15to50_total[context] <- getStatValue(shldprhwy_15to50_previous, "shldprhwy_15to50")
    shldtchwy_0to5_total[context] <- getStatValue(shldtchwy_0to5_previous, "shldtchwy_0to5")
    shldtchwy_5to15_total[context] <- getStatValue(shldtchwy_5to15_previous, "shldtchwy_5to15")
    shldtchwy_15to50_total[context] <- getStatValue(shldtchwy_15to50_previous, "shldtchwy_15to50")
}

if (!is.null(country_code4[[1]]) && !is.na(country_code4)) {
    a0_total[context] <- getStatValue(a0_previous, "a0")
    a1_total[context] <- getStatValue(a1_previous, "a1")
    a2_total[context] <- getStatValue(a2_previous, "a2")
    a3_total[context] <- getStatValue(a3_previous, "a3")
    a4_total[context] <- getStatValue(a4_previous, "a4")
    a5_total[context] <- getStatValue(a5_previous, "a5")
    aa_total[context] <- getStatValue(aa_previous, "aa")
    ap_total[context] <- getStatValue(ap_previous, "ap")
    b_total[context] <- getStatValue(b_previous, "b")
    cr_total[context] <- getStatValue(cr_previous, "cr")
    sa_total[context] <- getStatValue(sa_previous, "sa")
    x1_total[context] <- getStatValue(x1_previous, "x1")
    x2_total[context] <- getStatValue(x2_previous, "x2")
    f_total[context] <- getStatValue(f_previous, "f")
    fi_total[context] <- getStatValue(fi_previous, "fi")
    fr_total[context] <- getStatValue(fr_previous, "fr")
    g1_total[context] <- getStatValue(g1_previous, "g1")
    g2_total[context] <- getStatValue(g2_previous, "g2")
    g3_total[context] <- getStatValue(g3_previous, "g3")
    g4_total[context] <- getStatValue(g4_previous, "g4")
    g5_total[context] <- getStatValue(g5_previous, "g5")
    g6_total[context] <- getStatValue(g6_previous, "g6")
    gf_total[context] <- getStatValue(gf_previous, "gf")
    lc_total[context] <- getStatValue(lc_previous, "lc")
    lu_total[context] <- getStatValue(lu_previous, "lu")
    nf_total[context] <- getStatValue(nf_previous, "nf")
    ow_total[context] <- getStatValue(ow_previous, "ow")
    p_total[context] <- getStatValue(p_previous, "p")
    pc_total[context] <- getStatValue(pc_previous, "pc")
    pci_total[context] <- getStatValue(pci_previous, "pci")
    pcp_total[context] <- getStatValue(pcp_previous, "pcp")
    pu_total[context] <- getStatValue(pu_previous, "pu")
    r_total[context] <- getStatValue(r_previous, "r")
    re_total[context] <- getStatValue(re_previous, "re")
    s1_total[context] <- getStatValue(s1_previous, "s1")
    s2_total[context] <- getStatValue(s2_previous, "s2")
    s3_total[context] <- getStatValue(s3_previous, "s3")
    s4_total[context] <- getStatValue(s4_previous, "s4")
    s5_total[context] <- getStatValue(s5_previous, "s5")
    s6_total[context] <- getStatValue(s6_previous, "s6")
    st_total[context] <- getStatValue(st_previous, "st")
    u_total[context] <- getStatValue(u_previous, "u")
    wm_total[context] <- getStatValue(wm_previous, "wm")
    w_total[context] <- getStatValue(w_previous, "w")
    zl_total[context] <- getStatValue(zl_previous, "zl")
    zp_total[context] <- getStatValue(zp_previous, "zp")

    Length_s1[context] <- getStatLength(s1_previous)
    Length_s2[context] <- getStatLength(s2_previous)
    Length_s3[context] <- getStatLength(s3_previous)
    Length_s4[context] <- getStatLength(s4_previous)
    Length_s5[context] <- getStatLength(s5_previous)
    Length_s6[context] <- getStatLength(s6_previous)
}
