Develop a data visualization application
========================================

*“We are trying to get a sense of your ability to turn data into a
functional web application. We would rather see something cool than have
you just follow the instructions.”*

### Approach

The first step in developing this data-oriented application is to
understand the information that this data is intended to convey. The
title of the dataset gives some indication of its content. My overall
sense of what kinds of experiences can be meaningfully facilitated by
the information contained therein can be greatly enhanced by 1) A
description of each field, 2) Knowledge of how the data was gathered and
maintained, and 3) Some exploratory operations (statistical aggregation,
plotting, mapping, etc.) on the dataset elements/features.

1.  A description of the fields was provided. Among these, the
    `database` field is described as naming the source of the given
    record in the dataset, which will help with step **2**. The fields
    labeled `geoid_co` and `geoid_st` respectively contain county and
    state level FIPS identifiers, which is useful for sorting and
    grouping the data by county/state while in its tabular form
    (i.e. pre-visualization filters). `the_geom` field contains geometry
    in a binary format, which can be transformed into GeoJSON or WKT. To
    minimize the size of the query response, I removed both `the_geom`
    and `the_geom_webmercator` from the Carto SQL request and replaced
    them with `ST_AsText(the_geom) as geom` (WKT), for subsequent
    geospatial processing.

2.  Values in the `database` field include [Underlying Cause of Death
    1999-2018 - CDC
    Wonder](https://wonder.cdc.gov/wonder/help/ucd.html), a website
    maintained by the CDC which describes itself as " -level national
    mortality and population data," captured from death certificates.
    For reference, I downloaded the complete technical instructions that
    correspond to this dataset (*Note*: the documentation available
    online is for the more recent “1999 - 2019” dataset). By comparing
    the descriptions of the fields in the given dataset to the
    documentation for “Underlying Cause of Death 1999-2019”, I can see
    that there are additional fields that have been joined to the
    original “Underlying Cause of Death 1999-2018” dataset in some
    unknown way, but presumably keyed on the location (by county FIPS)
    that each record represents. The values found in the cause of death
    field (`death_cause`) do **not** correspond to the (Cause of Death
    (ICD-10))\[<a href="https://wonder.cdc.gov/wonder/help/ucd.html#ICD-10%20Codes" class="uri">https://wonder.cdc.gov/wonder/help/ucd.html#ICD-10%20Codes</a>\]
    codes listed in the technical documentation, indicating that this
    dataset has already been transformed in some non-trivial way as
    compared to one of its source (i.e., “Underlying Cause of Death
    1999-2018”) and the concept of “Deaths of Despair” seems to come
    from another source entirely, along with any information relating to
    Covid-19. `death_cause = "DoD"` may actually be an aggregation of
    the other four values found in the data, but I wasn’t able to
    determine if that is the case.

3.  R was used to read in the JSON data from Rural Innovation’s Carto
    platform and save it to local disk:

<!-- -->

    data_file <- paste0(working_dir, "/content/data/dod_covid_county.RData")
    if (!file.exists(data_file)) {
      dod_covid_county_data <- data.frame(jsonlite::fromJSON("https://ruralinnovation-admin.carto.com/api/v2/sql?q=select%20cartodb_id,fid,geoid_co,name,namelsad,st_stusps,geoid_st,st_name,land_sqmi,water_sqmi,lon,lat,acp_name,cbsa_type,rin_flag,database,geo_level,geoid_cbsa,geoid_acp,co_name,cbsa_name,cdc_urbanization,time_interval,time_period,death_cause,age_group,gender,race,population,deaths_dod,age_adjusted_rate,age_adjusted_rate_se,age_adjusted_rate_lower_95_ci,age_adjusted_rate_upper_95_ci,crude_rate,crude_rate_se,crude_rate_lower_95_ci,crude_rate_upper_95_ci,acp_image,pop,confirmed,deaths_covid,confirmed_per_100k,deaths_per_100k,ST_AsText(the_geom)%20as%20geom%20from%20%22ruralinnovation-admin%22.dod_covid_county", flatten = TRUE)$rows)
       save(dod_covid_county_data, file = data_file)
    }

    dataset <- load(data_file) # will import a data.frame called "dod_covid_county_data" from data_file
    dod_covid_county_data <- dod_covid_county_data[order(data.frame(list(dod_covid_county_data$geoid_co))), ] # Order by County FIPS
    str(dod_covid_county_data)

    ## 'data.frame':    62840 obs. of  45 variables:
    ##  $ cartodb_id                   : int  1203 1205 1207 1209 1211 1213 1215 1219 1217 1225 ...
    ##  $ fid                          : int  1 2 3 4 5 6 7 9 8 11 ...
    ##  $ geoid_co                     : chr  "01001" "01001" "01001" "01001" ...
    ##  $ name                         : chr  "Autauga" "Autauga" "Autauga" "Autauga" ...
    ##  $ namelsad                     : chr  "Autauga County" "Autauga County" "Autauga County" "Autauga County" ...
    ##  $ st_stusps                    : chr  "AL" "AL" "AL" "AL" ...
    ##  $ geoid_st                     : chr  "01" "01" "01" "01" ...
    ##  $ st_name                      : chr  "Alabama" "Alabama" "Alabama" "Alabama" ...
    ##  $ land_sqmi                    : num  594 594 594 594 594 ...
    ##  $ water_sqmi                   : num  9.93 9.93 9.93 9.93 9.93 ...
    ##  $ lon                          : num  -86.6 -86.6 -86.6 -86.6 -86.6 ...
    ##  $ lat                          : num  32.5 32.5 32.5 32.5 32.5 ...
    ##  $ acp_name                     : chr  "Exurbs" "Exurbs" "Exurbs" "Exurbs" ...
    ##  $ cbsa_type                    : chr  "Metropolitan Statistical Areas" "Metropolitan Statistical Areas" "Metropolitan Statistical Areas" "Metropolitan Statistical Areas" ...
    ##  $ rin_flag                     : int  0 0 0 0 0 0 0 0 0 0 ...
    ##  $ database                     : chr  "Underlying Cause of Death 1999-2018 - CDC Wonder" "Underlying Cause of Death 1999-2018 - CDC Wonder" "Underlying Cause of Death 1999-2018 - CDC Wonder" "Underlying Cause of Death 1999-2018 - CDC Wonder" ...
    ##  $ geo_level                    : chr  "county" "county" "county" "county" ...
    ##  $ geoid_cbsa                   : chr  "33860" "33860" "33860" "33860" ...
    ##  $ geoid_acp                    : int  1 1 1 1 1 1 1 1 1 1 ...
    ##  $ co_name                      : chr  "Autauga" "Autauga" "Autauga" "Autauga" ...
    ##  $ cbsa_name                    : chr  "Montgomery, AL" "Montgomery, AL" "Montgomery, AL" "Montgomery, AL" ...
    ##  $ cdc_urbanization             : chr  "Medium Metro" "Medium Metro" "Medium Metro" "Medium Metro" ...
    ##  $ time_interval                : chr  "Years Aggregate" "Years Aggregate" "Years Aggregate" "Years Aggregate" ...
    ##  $ time_period                  : chr  "1999-2003" "2004-2008" "2009-2013" "2014-2018" ...
    ##  $ death_cause                  : chr  "Alcohol" "Alcohol" "Alcohol" "Alcohol" ...
    ##  $ age_group                    : chr  "All" "All" "All" "All" ...
    ##  $ gender                       : chr  "All" "All" "All" "All" ...
    ##  $ race                         : chr  "All" "All" "All" "All" ...
    ##  $ population                   : int  224232 255052 274733 277263 224232 255052 274733 224232 277263 274733 ...
    ##  $ deaths_dod                   : int  10 13 0 13 14 12 32 54 27 83 ...
    ##  $ age_adjusted_rate            : num  4.5 5.1 NA 4.7 6.2 4.7 10.9 24.2 8.5 29.7 ...
    ##  $ age_adjusted_rate_se         : num  1.4 1.3 NA 1.2 1.9 1.4 2 3.3 1.6 3.3 ...
    ##  $ age_adjusted_rate_lower_95_ci: num  2.1 2.5 NA 2.2 3.8 2.5 7.4 18.2 5.6 23.6 ...
    ##  $ age_adjusted_rate_upper_95_ci: num  8.2 8.1 NA 7.6 12.1 8.4 15.5 31.7 12.3 36.9 ...
    ##  $ crude_rate                   : num  4.46 5.1 NA 4.69 6.24 ...
    ##  $ crude_rate_se                : num  1.41 1.41 NA 1.3 1.67 ...
    ##  $ crude_rate_lower_95_ci       : num  2.14 2.71 NA 2.5 3.41 ...
    ##  $ crude_rate_upper_95_ci       : num  8.2 8.72 NA 8.02 10.48 ...
    ##  $ acp_image                    : chr  "https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" "https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" "https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" "https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" ...
    ##  $ pop                          : int  55869 55869 55869 55869 55869 55869 55869 55869 55869 55869 ...
    ##  $ confirmed                    : int  5499 5499 5499 5499 5499 5499 5499 5499 5499 5499 ...
    ##  $ deaths_covid                 : int  67 67 67 67 67 67 67 67 67 67 ...
    ##  $ confirmed_per_100k           : num  9843 9843 9843 9843 9843 ...
    ##  $ deaths_per_100k              : num  120 120 120 120 120 ...
    ##  $ geom                         : chr  "MULTIPOLYGON(((-86.921196 32.657542,-86.920352 32.658563,-86.920411 32.660077,-86.917595 32.664169,-86.91461 32"| __truncated__ "MULTIPOLYGON(((-86.921196 32.657542,-86.920352 32.658563,-86.920411 32.660077,-86.917595 32.664169,-86.91461 32"| __truncated__ "MULTIPOLYGON(((-86.921196 32.657542,-86.920352 32.658563,-86.920411 32.660077,-86.917595 32.664169,-86.91461 32"| __truncated__ "MULTIPOLYGON(((-86.921196 32.657542,-86.920352 32.658563,-86.920411 32.660077,-86.917595 32.664169,-86.91461 32"| __truncated__ ...

Having read portions of the “Underlying Cause of Death 1999-2019”
technical documentation and looking at the data itself, I can see that
there are multiple records associated with each place (county), so it
would be good to filter records by an additional characteristic, in this
case, Cause of Death (`death_cause`), before beginning to look deeper at
each “bucket” of data that is produced.

The five causes of death included in this data are:

1 2 3 4 5

… again, suspecting that `death_cause = "DoD"` may be an aggregate term
in respect to the other four causes. The data corresponding to each of
these causes has been filtered into its own object/table using R
(comments include Carto SQL queries to do the same).

    death_by_alcohol_data <- dod_covid_county_data[grepl("Alcohol", dod_covid_county_data[, 25]), ] #dod_covid_county_data %>% dplyr::filter(death_cause = "Alcohol")
    #data.frame(jsonlite::fromJSON("https://ruralinnovation-admin.carto.com/api/v2/sql?q=select%20cartodb_id,fid,geoid_co,name,namelsad,st_stusps,geoid_st,st_name,land_sqmi,water_sqmi,lon,lat,acp_name,cbsa_type,rin_flag,database,geo_level,geoid_cbsa,geoid_acp,co_name,cbsa_name,cdc_urbanization,time_interval,time_period,death_cause,age_group,gender,race,population,deaths_dod,age_adjusted_rate,age_adjusted_rate_se,age_adjusted_rate_lower_95_ci,age_adjusted_rate_upper_95_ci,crude_rate,crude_rate_se,crude_rate_lower_95_ci,crude_rate_upper_95_ci,acp_image,pop,confirmed,deaths_covid,confirmed_per_100k,deaths_per_100k,ST_AsText(the_geom)%20as%20geom%20from%20%22ruralinnovation-admin%22.dod_covid_county%20where%20death_cause%20ilike%20%27Alcohol%27", flatten = TRUE)$rows)

    death_by_alcohol_data %>% showTablePreivew

<table>
<thead>
<tr class="header">
<th></th>
<th style="text-align: right;">cartodb_id</th>
<th style="text-align: right;">fid</th>
<th style="text-align: left;">geoid_co</th>
<th style="text-align: left;">name</th>
<th style="text-align: left;">namelsad</th>
<th style="text-align: left;">st_stusps</th>
<th style="text-align: left;">geoid_st</th>
<th style="text-align: left;">st_name</th>
<th style="text-align: right;">land_sqmi</th>
<th style="text-align: right;">water_sqmi</th>
<th style="text-align: right;">lon</th>
<th style="text-align: right;">lat</th>
<th style="text-align: left;">acp_name</th>
<th style="text-align: left;">cbsa_type</th>
<th style="text-align: right;">rin_flag</th>
<th style="text-align: left;">database</th>
<th style="text-align: left;">geo_level</th>
<th style="text-align: left;">geoid_cbsa</th>
<th style="text-align: right;">geoid_acp</th>
<th style="text-align: left;">co_name</th>
<th style="text-align: left;">cbsa_name</th>
<th style="text-align: left;">cdc_urbanization</th>
<th style="text-align: center;">time_interval</th>
<th style="text-align: center;">time_period</th>
<th style="text-align: left;">death_cause</th>
<th style="text-align: left;">age_group</th>
<th style="text-align: left;">gender</th>
<th style="text-align: left;">race</th>
<th style="text-align: right;">population</th>
<th style="text-align: right;">deaths_dod</th>
<th style="text-align: right;">age_adjusted_rate</th>
<th style="text-align: right;">age_adjusted_rate_se</th>
<th style="text-align: right;">age_adjusted_rate_lower_95_ci</th>
<th style="text-align: right;">age_adjusted_rate_upper_95_ci</th>
<th style="text-align: right;">crude_rate</th>
<th style="text-align: right;">crude_rate_se</th>
<th style="text-align: right;">crude_rate_lower_95_ci</th>
<th style="text-align: right;">crude_rate_upper_95_ci</th>
<th style="text-align: left;">acp_image</th>
<th style="text-align: right;">pop</th>
<th style="text-align: right;">confirmed</th>
<th style="text-align: right;">deaths_covid</th>
<th style="text-align: right;">confirmed_per_100k</th>
<th style="text-align: right;">deaths_per_100k</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>1171</td>
<td style="text-align: right;">1203</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">Alcohol</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">224232</td>
<td style="text-align: right;">10</td>
<td style="text-align: right;">4.5</td>
<td style="text-align: right;">1.4</td>
<td style="text-align: right;">2.1</td>
<td style="text-align: right;">8.2</td>
<td style="text-align: right;">4.459667</td>
<td style="text-align: right;">1.410</td>
<td style="text-align: right;">2.139</td>
<td style="text-align: right;">8.202</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1174</td>
<td style="text-align: right;">1205</td>
<td style="text-align: right;">2</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2004-2008</td>
<td style="text-align: left;">Alcohol</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">255052</td>
<td style="text-align: right;">13</td>
<td style="text-align: right;">5.1</td>
<td style="text-align: right;">1.3</td>
<td style="text-align: right;">2.5</td>
<td style="text-align: right;">8.1</td>
<td style="text-align: right;">5.097000</td>
<td style="text-align: right;">1.414</td>
<td style="text-align: right;">2.714</td>
<td style="text-align: right;">8.716</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1177</td>
<td style="text-align: right;">1207</td>
<td style="text-align: right;">3</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2009-2013</td>
<td style="text-align: left;">Alcohol</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">274733</td>
<td style="text-align: right;">0</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1180</td>
<td style="text-align: right;">1209</td>
<td style="text-align: right;">4</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2014-2018</td>
<td style="text-align: left;">Alcohol</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">277263</td>
<td style="text-align: right;">13</td>
<td style="text-align: right;">4.7</td>
<td style="text-align: right;">1.2</td>
<td style="text-align: right;">2.2</td>
<td style="text-align: right;">7.6</td>
<td style="text-align: right;">4.688689</td>
<td style="text-align: right;">1.300</td>
<td style="text-align: right;">2.497</td>
<td style="text-align: right;">8.018</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1127</td>
<td style="text-align: right;">1166</td>
<td style="text-align: right;">24</td>
<td style="text-align: left;">01003</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Baldwin County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">1589.79</td>
<td style="text-align: right;">437.47400</td>
<td style="text-align: right;">-87.72256</td>
<td style="text-align: right;">30.72748</td>
<td style="text-align: left;">Graying America</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">19300</td>
<td style="text-align: right;">2</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Daphne-Fairhope-Foley, AL</td>
<td style="text-align: left;">Small Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2014-2018</td>
<td style="text-align: left;">Alcohol</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">1043033</td>
<td style="text-align: right;">83</td>
<td style="text-align: right;">6.1</td>
<td style="text-align: right;">0.7</td>
<td style="text-align: right;">4.8</td>
<td style="text-align: right;">7.6</td>
<td style="text-align: right;">7.957562</td>
<td style="text-align: right;">0.873</td>
<td style="text-align: right;">6.338</td>
<td style="text-align: right;">9.865</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg</a></td>
<td style="text-align: right;">223234</td>
<td style="text-align: right;">17627</td>
<td style="text-align: right;">217</td>
<td style="text-align: right;">7896.199</td>
<td style="text-align: right;">97.20741</td>
</tr>
</tbody>
</table>

    death_by_cirrhosis_data <- dod_covid_county_data[grepl("Cirrhosis", dod_covid_county_data[, 25]), ] #dod_covid_county_data %>% dplyr::filter(death_cause = "Cirrhosis")
    #data.frame(jsonlite::fromJSON("https://ruralinnovation-admin.carto.com/api/v2/sql?q=select%20cartodb_id,fid,geoid_co,name,namelsad,st_stusps,geoid_st,st_name,land_sqmi,water_sqmi,lon,lat,acp_name,cbsa_type,rin_flag,database,geo_level,geoid_cbsa,geoid_acp,co_name,cbsa_name,cdc_urbanization,time_interval,time_period,death_cause,age_group,gender,race,population,deaths_dod,age_adjusted_rate,age_adjusted_rate_se,age_adjusted_rate_lower_95_ci,age_adjusted_rate_upper_95_ci,crude_rate,crude_rate_se,crude_rate_lower_95_ci,crude_rate_upper_95_ci,acp_image,pop,confirmed,deaths_covid,confirmed_per_100k,deaths_per_100k,ST_AsText(the_geom)%20as%20geom%20from%20%22ruralinnovation-admin%22.dod_covid_county%20where%20death_cause%20ilike%20%27Cirrhosis%27", flatten = TRUE)$rows)

    death_by_cirrhosis_data %>% showTablePreivew

<table>
<thead>
<tr class="header">
<th></th>
<th style="text-align: right;">cartodb_id</th>
<th style="text-align: right;">fid</th>
<th style="text-align: left;">geoid_co</th>
<th style="text-align: left;">name</th>
<th style="text-align: left;">namelsad</th>
<th style="text-align: left;">st_stusps</th>
<th style="text-align: left;">geoid_st</th>
<th style="text-align: left;">st_name</th>
<th style="text-align: right;">land_sqmi</th>
<th style="text-align: right;">water_sqmi</th>
<th style="text-align: right;">lon</th>
<th style="text-align: right;">lat</th>
<th style="text-align: left;">acp_name</th>
<th style="text-align: left;">cbsa_type</th>
<th style="text-align: right;">rin_flag</th>
<th style="text-align: left;">database</th>
<th style="text-align: left;">geo_level</th>
<th style="text-align: left;">geoid_cbsa</th>
<th style="text-align: right;">geoid_acp</th>
<th style="text-align: left;">co_name</th>
<th style="text-align: left;">cbsa_name</th>
<th style="text-align: left;">cdc_urbanization</th>
<th style="text-align: center;">time_interval</th>
<th style="text-align: center;">time_period</th>
<th style="text-align: left;">death_cause</th>
<th style="text-align: left;">age_group</th>
<th style="text-align: left;">gender</th>
<th style="text-align: left;">race</th>
<th style="text-align: right;">population</th>
<th style="text-align: right;">deaths_dod</th>
<th style="text-align: right;">age_adjusted_rate</th>
<th style="text-align: right;">age_adjusted_rate_se</th>
<th style="text-align: right;">age_adjusted_rate_lower_95_ci</th>
<th style="text-align: right;">age_adjusted_rate_upper_95_ci</th>
<th style="text-align: right;">crude_rate</th>
<th style="text-align: right;">crude_rate_se</th>
<th style="text-align: right;">crude_rate_lower_95_ci</th>
<th style="text-align: right;">crude_rate_upper_95_ci</th>
<th style="text-align: left;">acp_image</th>
<th style="text-align: right;">pop</th>
<th style="text-align: right;">confirmed</th>
<th style="text-align: right;">deaths_covid</th>
<th style="text-align: right;">confirmed_per_100k</th>
<th style="text-align: right;">deaths_per_100k</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>1182</td>
<td style="text-align: right;">1211</td>
<td style="text-align: right;">5</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">Cirrhosis</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">224232</td>
<td style="text-align: right;">14</td>
<td style="text-align: right;">6.2</td>
<td style="text-align: right;">1.9</td>
<td style="text-align: right;">3.8</td>
<td style="text-align: right;">12.1</td>
<td style="text-align: right;">6.243533</td>
<td style="text-align: right;">1.669</td>
<td style="text-align: right;">3.413</td>
<td style="text-align: right;">10.476</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1186</td>
<td style="text-align: right;">1213</td>
<td style="text-align: right;">6</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2004-2008</td>
<td style="text-align: left;">Cirrhosis</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">255052</td>
<td style="text-align: right;">12</td>
<td style="text-align: right;">4.7</td>
<td style="text-align: right;">1.4</td>
<td style="text-align: right;">2.5</td>
<td style="text-align: right;">8.4</td>
<td style="text-align: right;">4.704923</td>
<td style="text-align: right;">1.358</td>
<td style="text-align: right;">2.431</td>
<td style="text-align: right;">8.219</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1189</td>
<td style="text-align: right;">1215</td>
<td style="text-align: right;">7</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2009-2013</td>
<td style="text-align: left;">Cirrhosis</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">274733</td>
<td style="text-align: right;">32</td>
<td style="text-align: right;">10.9</td>
<td style="text-align: right;">2.0</td>
<td style="text-align: right;">7.4</td>
<td style="text-align: right;">15.5</td>
<td style="text-align: right;">11.647673</td>
<td style="text-align: right;">2.059</td>
<td style="text-align: right;">7.967</td>
<td style="text-align: right;">16.443</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1192</td>
<td style="text-align: right;">1217</td>
<td style="text-align: right;">8</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2014-2018</td>
<td style="text-align: left;">Cirrhosis</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">277263</td>
<td style="text-align: right;">27</td>
<td style="text-align: right;">8.5</td>
<td style="text-align: right;">1.6</td>
<td style="text-align: right;">5.6</td>
<td style="text-align: right;">12.3</td>
<td style="text-align: right;">9.738046</td>
<td style="text-align: right;">1.874</td>
<td style="text-align: right;">6.417</td>
<td style="text-align: right;">14.168</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1128</td>
<td style="text-align: right;">1167</td>
<td style="text-align: right;">25</td>
<td style="text-align: left;">01003</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Baldwin County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">1589.79</td>
<td style="text-align: right;">437.47400</td>
<td style="text-align: right;">-87.72256</td>
<td style="text-align: right;">30.72748</td>
<td style="text-align: left;">Graying America</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">19300</td>
<td style="text-align: right;">2</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Daphne-Fairhope-Foley, AL</td>
<td style="text-align: left;">Small Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">Cirrhosis</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">722311</td>
<td style="text-align: right;">52</td>
<td style="text-align: right;">6.1</td>
<td style="text-align: right;">0.9</td>
<td style="text-align: right;">4.5</td>
<td style="text-align: right;">8.0</td>
<td style="text-align: right;">7.199115</td>
<td style="text-align: right;">0.998</td>
<td style="text-align: right;">5.377</td>
<td style="text-align: right;">9.441</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg</a></td>
<td style="text-align: right;">223234</td>
<td style="text-align: right;">17627</td>
<td style="text-align: right;">217</td>
<td style="text-align: right;">7896.199</td>
<td style="text-align: right;">97.20741</td>
</tr>
</tbody>
</table>

    death_by_drug_data <- dod_covid_county_data[grepl("Drug", dod_covid_county_data[, 25]), ] #dod_covid_county_data %>% dplyr::filter(death_cause = "Drug")
    #data.frame(jsonlite::fromJSON("https://ruralinnovation-admin.carto.com/api/v2/sql?q=select%20cartodb_id,fid,geoid_co,name,namelsad,st_stusps,geoid_st,st_name,land_sqmi,water_sqmi,lon,lat,acp_name,cbsa_type,rin_flag,database,geo_level,geoid_cbsa,geoid_acp,co_name,cbsa_name,cdc_urbanization,time_interval,time_period,death_cause,age_group,gender,race,population,deaths_dod,age_adjusted_rate,age_adjusted_rate_se,age_adjusted_rate_lower_95_ci,age_adjusted_rate_upper_95_ci,crude_rate,crude_rate_se,crude_rate_lower_95_ci,crude_rate_upper_95_ci,acp_image,pop,confirmed,deaths_covid,confirmed_per_100k,deaths_per_100k,ST_AsText(the_geom)%20as%20geom%20from%20%22ruralinnovation-admin%22.dod_covid_county%20where%20death_cause%20ilike%20%27Drug%27", flatten = TRUE)$rows)

    death_by_drug_data %>% showTablePreivew

<table>
<thead>
<tr class="header">
<th></th>
<th style="text-align: right;">cartodb_id</th>
<th style="text-align: right;">fid</th>
<th style="text-align: left;">geoid_co</th>
<th style="text-align: left;">name</th>
<th style="text-align: left;">namelsad</th>
<th style="text-align: left;">st_stusps</th>
<th style="text-align: left;">geoid_st</th>
<th style="text-align: left;">st_name</th>
<th style="text-align: right;">land_sqmi</th>
<th style="text-align: right;">water_sqmi</th>
<th style="text-align: right;">lon</th>
<th style="text-align: right;">lat</th>
<th style="text-align: left;">acp_name</th>
<th style="text-align: left;">cbsa_type</th>
<th style="text-align: right;">rin_flag</th>
<th style="text-align: left;">database</th>
<th style="text-align: left;">geo_level</th>
<th style="text-align: left;">geoid_cbsa</th>
<th style="text-align: right;">geoid_acp</th>
<th style="text-align: left;">co_name</th>
<th style="text-align: left;">cbsa_name</th>
<th style="text-align: left;">cdc_urbanization</th>
<th style="text-align: center;">time_interval</th>
<th style="text-align: center;">time_period</th>
<th style="text-align: left;">death_cause</th>
<th style="text-align: left;">age_group</th>
<th style="text-align: left;">gender</th>
<th style="text-align: left;">race</th>
<th style="text-align: right;">population</th>
<th style="text-align: right;">deaths_dod</th>
<th style="text-align: right;">age_adjusted_rate</th>
<th style="text-align: right;">age_adjusted_rate_se</th>
<th style="text-align: right;">age_adjusted_rate_lower_95_ci</th>
<th style="text-align: right;">age_adjusted_rate_upper_95_ci</th>
<th style="text-align: right;">crude_rate</th>
<th style="text-align: right;">crude_rate_se</th>
<th style="text-align: right;">crude_rate_lower_95_ci</th>
<th style="text-align: right;">crude_rate_upper_95_ci</th>
<th style="text-align: left;">acp_image</th>
<th style="text-align: right;">pop</th>
<th style="text-align: right;">confirmed</th>
<th style="text-align: right;">deaths_covid</th>
<th style="text-align: right;">confirmed_per_100k</th>
<th style="text-align: right;">deaths_per_100k</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>1204</td>
<td style="text-align: right;">1233</td>
<td style="text-align: right;">13</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">Drug</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">224232</td>
<td style="text-align: right;">0</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: right;">NA</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1213</td>
<td style="text-align: right;">1238</td>
<td style="text-align: right;">14</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2004-2008</td>
<td style="text-align: left;">Drug</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">255052</td>
<td style="text-align: right;">19</td>
<td style="text-align: right;">7.4</td>
<td style="text-align: right;">1.8</td>
<td style="text-align: right;">4.7</td>
<td style="text-align: right;">12.1</td>
<td style="text-align: right;">7.449461</td>
<td style="text-align: right;">1.709</td>
<td style="text-align: right;">4.485</td>
<td style="text-align: right;">11.633</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1216</td>
<td style="text-align: right;">1242</td>
<td style="text-align: right;">15</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2009-2013</td>
<td style="text-align: left;">Drug</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">274733</td>
<td style="text-align: right;">21</td>
<td style="text-align: right;">7.9</td>
<td style="text-align: right;">1.7</td>
<td style="text-align: right;">4.9</td>
<td style="text-align: right;">12.0</td>
<td style="text-align: right;">7.643785</td>
<td style="text-align: right;">1.668</td>
<td style="text-align: right;">4.732</td>
<td style="text-align: right;">11.684</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1220</td>
<td style="text-align: right;">1245</td>
<td style="text-align: right;">16</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2014-2018</td>
<td style="text-align: left;">Drug</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">277263</td>
<td style="text-align: right;">28</td>
<td style="text-align: right;">11.0</td>
<td style="text-align: right;">2.1</td>
<td style="text-align: right;">7.3</td>
<td style="text-align: right;">15.9</td>
<td style="text-align: right;">10.098715</td>
<td style="text-align: right;">1.908</td>
<td style="text-align: right;">6.710</td>
<td style="text-align: right;">14.595</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1138</td>
<td style="text-align: right;">1175</td>
<td style="text-align: right;">33</td>
<td style="text-align: left;">01003</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Baldwin County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">1589.79</td>
<td style="text-align: right;">437.47400</td>
<td style="text-align: right;">-87.72256</td>
<td style="text-align: right;">30.72748</td>
<td style="text-align: left;">Graying America</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">19300</td>
<td style="text-align: right;">2</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Daphne-Fairhope-Foley, AL</td>
<td style="text-align: left;">Small Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">Drug</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">722311</td>
<td style="text-align: right;">54</td>
<td style="text-align: right;">7.8</td>
<td style="text-align: right;">1.1</td>
<td style="text-align: right;">5.9</td>
<td style="text-align: right;">10.3</td>
<td style="text-align: right;">7.476004</td>
<td style="text-align: right;">1.017</td>
<td style="text-align: right;">5.616</td>
<td style="text-align: right;">9.755</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg</a></td>
<td style="text-align: right;">223234</td>
<td style="text-align: right;">17627</td>
<td style="text-align: right;">217</td>
<td style="text-align: right;">7896.199</td>
<td style="text-align: right;">97.20741</td>
</tr>
</tbody>
</table>

    death_by_suicide_data <- dod_covid_county_data[grepl("Suicide", dod_covid_county_data[, 25]), ] #dod_covid_county_data %>% dplyr::filter(death_cause = "Suicide")
    #data.frame(jsonlite::fromJSON("https://ruralinnovation-admin.carto.com/api/v2/sql?q=select%20cartodb_id,fid,geoid_co,name,namelsad,st_stusps,geoid_st,st_name,land_sqmi,water_sqmi,lon,lat,acp_name,cbsa_type,rin_flag,database,geo_level,geoid_cbsa,geoid_acp,co_name,cbsa_name,cdc_urbanization,time_interval,time_period,death_cause,age_group,gender,race,population,deaths_dod,age_adjusted_rate,age_adjusted_rate_se,age_adjusted_rate_lower_95_ci,age_adjusted_rate_upper_95_ci,crude_rate,crude_rate_se,crude_rate_lower_95_ci,crude_rate_upper_95_ci,acp_image,pop,confirmed,deaths_covid,confirmed_per_100k,deaths_per_100k,ST_AsText(the_geom)%20as%20geom%20from%20%22ruralinnovation-admin%22.dod_covid_county%20where%20death_cause%20ilike%20%27Suicide%27", flatten = TRUE)$rows)

    death_by_suicide_data %>% showTablePreivew

<table>
<thead>
<tr class="header">
<th></th>
<th style="text-align: right;">cartodb_id</th>
<th style="text-align: right;">fid</th>
<th style="text-align: left;">geoid_co</th>
<th style="text-align: left;">name</th>
<th style="text-align: left;">namelsad</th>
<th style="text-align: left;">st_stusps</th>
<th style="text-align: left;">geoid_st</th>
<th style="text-align: left;">st_name</th>
<th style="text-align: right;">land_sqmi</th>
<th style="text-align: right;">water_sqmi</th>
<th style="text-align: right;">lon</th>
<th style="text-align: right;">lat</th>
<th style="text-align: left;">acp_name</th>
<th style="text-align: left;">cbsa_type</th>
<th style="text-align: right;">rin_flag</th>
<th style="text-align: left;">database</th>
<th style="text-align: left;">geo_level</th>
<th style="text-align: left;">geoid_cbsa</th>
<th style="text-align: right;">geoid_acp</th>
<th style="text-align: left;">co_name</th>
<th style="text-align: left;">cbsa_name</th>
<th style="text-align: left;">cdc_urbanization</th>
<th style="text-align: center;">time_interval</th>
<th style="text-align: center;">time_period</th>
<th style="text-align: left;">death_cause</th>
<th style="text-align: left;">age_group</th>
<th style="text-align: left;">gender</th>
<th style="text-align: left;">race</th>
<th style="text-align: right;">population</th>
<th style="text-align: right;">deaths_dod</th>
<th style="text-align: right;">age_adjusted_rate</th>
<th style="text-align: right;">age_adjusted_rate_se</th>
<th style="text-align: right;">age_adjusted_rate_lower_95_ci</th>
<th style="text-align: right;">age_adjusted_rate_upper_95_ci</th>
<th style="text-align: right;">crude_rate</th>
<th style="text-align: right;">crude_rate_se</th>
<th style="text-align: right;">crude_rate_lower_95_ci</th>
<th style="text-align: right;">crude_rate_upper_95_ci</th>
<th style="text-align: left;">acp_image</th>
<th style="text-align: right;">pop</th>
<th style="text-align: right;">confirmed</th>
<th style="text-align: right;">deaths_covid</th>
<th style="text-align: right;">confirmed_per_100k</th>
<th style="text-align: right;">deaths_per_100k</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>1225</td>
<td style="text-align: right;">1250</td>
<td style="text-align: right;">17</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">Suicide</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">224232</td>
<td style="text-align: right;">37</td>
<td style="text-align: right;">16.6</td>
<td style="text-align: right;">2.8</td>
<td style="text-align: right;">11.6</td>
<td style="text-align: right;">23.0</td>
<td style="text-align: right;">16.50077</td>
<td style="text-align: right;">2.713</td>
<td style="text-align: right;">11.618</td>
<td style="text-align: right;">22.744</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1230</td>
<td style="text-align: right;">1254</td>
<td style="text-align: right;">18</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2004-2008</td>
<td style="text-align: left;">Suicide</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">255052</td>
<td style="text-align: right;">32</td>
<td style="text-align: right;">12.6</td>
<td style="text-align: right;">2.2</td>
<td style="text-align: right;">8.6</td>
<td style="text-align: right;">17.8</td>
<td style="text-align: right;">12.54646</td>
<td style="text-align: right;">2.218</td>
<td style="text-align: right;">8.582</td>
<td style="text-align: right;">17.712</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1233</td>
<td style="text-align: right;">1258</td>
<td style="text-align: right;">19</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2009-2013</td>
<td style="text-align: left;">Suicide</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">274733</td>
<td style="text-align: right;">53</td>
<td style="text-align: right;">18.6</td>
<td style="text-align: right;">2.6</td>
<td style="text-align: right;">13.9</td>
<td style="text-align: right;">24.4</td>
<td style="text-align: right;">19.29146</td>
<td style="text-align: right;">2.650</td>
<td style="text-align: right;">14.451</td>
<td style="text-align: right;">25.234</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1240</td>
<td style="text-align: right;">1260</td>
<td style="text-align: right;">20</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2014-2018</td>
<td style="text-align: left;">Suicide</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">277263</td>
<td style="text-align: right;">53</td>
<td style="text-align: right;">18.1</td>
<td style="text-align: right;">2.5</td>
<td style="text-align: right;">13.5</td>
<td style="text-align: right;">23.9</td>
<td style="text-align: right;">19.11542</td>
<td style="text-align: right;">2.626</td>
<td style="text-align: right;">14.319</td>
<td style="text-align: right;">25.003</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1145</td>
<td style="text-align: right;">1179</td>
<td style="text-align: right;">37</td>
<td style="text-align: left;">01003</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Baldwin County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">1589.79</td>
<td style="text-align: right;">437.47400</td>
<td style="text-align: right;">-87.72256</td>
<td style="text-align: right;">30.72748</td>
<td style="text-align: left;">Graying America</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">19300</td>
<td style="text-align: right;">2</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Daphne-Fairhope-Foley, AL</td>
<td style="text-align: left;">Small Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">Suicide</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">722311</td>
<td style="text-align: right;">113</td>
<td style="text-align: right;">15.6</td>
<td style="text-align: right;">1.5</td>
<td style="text-align: right;">12.7</td>
<td style="text-align: right;">18.6</td>
<td style="text-align: right;">15.64423</td>
<td style="text-align: right;">1.472</td>
<td style="text-align: right;">12.760</td>
<td style="text-align: right;">18.529</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg</a></td>
<td style="text-align: right;">223234</td>
<td style="text-align: right;">17627</td>
<td style="text-align: right;">217</td>
<td style="text-align: right;">7896.199</td>
<td style="text-align: right;">97.20741</td>
</tr>
</tbody>
</table>

    death_by_dod_data <- dod_covid_county_data[grepl("DoD", dod_covid_county_data[, 25]), ] #dod_covid_county_data %>% dplyr::filter(death_cause = "DoD" )
    #data.frame(jsonlite::fromJSON("https://ruralinnovation-admin.carto.com/api/v2/sql?q=select%20cartodb_id,fid,geoid_co,name,namelsad,st_stusps,geoid_st,st_name,land_sqmi,water_sqmi,lon,lat,acp_name,cbsa_type,rin_flag,database,geo_level,geoid_cbsa,geoid_acp,co_name,cbsa_name,cdc_urbanization,time_interval,time_period,death_cause,age_group,gender,race,population,deaths_dod,age_adjusted_rate,age_adjusted_rate_se,age_adjusted_rate_lower_95_ci,age_adjusted_rate_upper_95_ci,crude_rate,crude_rate_se,crude_rate_lower_95_ci,crude_rate_upper_95_ci,acp_image,pop,confirmed,deaths_covid,confirmed_per_100k,deaths_per_100k,ST_AsText(the_geom)%20as%20geom%20from%20%22ruralinnovation-admin%22.dod_covid_county%20where%20death_cause%20ilike%20%27DoD%27", flatten = TRUE)$rows)

    death_by_dod_data %>% showTablePreivew

<table>
<thead>
<tr class="header">
<th></th>
<th style="text-align: right;">cartodb_id</th>
<th style="text-align: right;">fid</th>
<th style="text-align: left;">geoid_co</th>
<th style="text-align: left;">name</th>
<th style="text-align: left;">namelsad</th>
<th style="text-align: left;">st_stusps</th>
<th style="text-align: left;">geoid_st</th>
<th style="text-align: left;">st_name</th>
<th style="text-align: right;">land_sqmi</th>
<th style="text-align: right;">water_sqmi</th>
<th style="text-align: right;">lon</th>
<th style="text-align: right;">lat</th>
<th style="text-align: left;">acp_name</th>
<th style="text-align: left;">cbsa_type</th>
<th style="text-align: right;">rin_flag</th>
<th style="text-align: left;">database</th>
<th style="text-align: left;">geo_level</th>
<th style="text-align: left;">geoid_cbsa</th>
<th style="text-align: right;">geoid_acp</th>
<th style="text-align: left;">co_name</th>
<th style="text-align: left;">cbsa_name</th>
<th style="text-align: left;">cdc_urbanization</th>
<th style="text-align: center;">time_interval</th>
<th style="text-align: center;">time_period</th>
<th style="text-align: left;">death_cause</th>
<th style="text-align: left;">age_group</th>
<th style="text-align: left;">gender</th>
<th style="text-align: left;">race</th>
<th style="text-align: right;">population</th>
<th style="text-align: right;">deaths_dod</th>
<th style="text-align: right;">age_adjusted_rate</th>
<th style="text-align: right;">age_adjusted_rate_se</th>
<th style="text-align: right;">age_adjusted_rate_lower_95_ci</th>
<th style="text-align: right;">age_adjusted_rate_upper_95_ci</th>
<th style="text-align: right;">crude_rate</th>
<th style="text-align: right;">crude_rate_se</th>
<th style="text-align: right;">crude_rate_lower_95_ci</th>
<th style="text-align: right;">crude_rate_upper_95_ci</th>
<th style="text-align: left;">acp_image</th>
<th style="text-align: right;">pop</th>
<th style="text-align: right;">confirmed</th>
<th style="text-align: right;">deaths_covid</th>
<th style="text-align: right;">confirmed_per_100k</th>
<th style="text-align: right;">deaths_per_100k</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td>1191</td>
<td style="text-align: right;">1219</td>
<td style="text-align: right;">9</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">DoD</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">224232</td>
<td style="text-align: right;">54</td>
<td style="text-align: right;">24.2</td>
<td style="text-align: right;">3.3</td>
<td style="text-align: right;">18.2</td>
<td style="text-align: right;">31.7</td>
<td style="text-align: right;">24.08220</td>
<td style="text-align: right;">3.277</td>
<td style="text-align: right;">18.091</td>
<td style="text-align: right;">31.422</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1194</td>
<td style="text-align: right;">1225</td>
<td style="text-align: right;">11</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2009-2013</td>
<td style="text-align: left;">DoD</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">274733</td>
<td style="text-align: right;">83</td>
<td style="text-align: right;">29.7</td>
<td style="text-align: right;">3.3</td>
<td style="text-align: right;">23.6</td>
<td style="text-align: right;">36.9</td>
<td style="text-align: right;">30.21115</td>
<td style="text-align: right;">3.316</td>
<td style="text-align: right;">24.063</td>
<td style="text-align: right;">37.451</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1195</td>
<td style="text-align: right;">1221</td>
<td style="text-align: right;">10</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2004-2008</td>
<td style="text-align: left;">DoD</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">255052</td>
<td style="text-align: right;">64</td>
<td style="text-align: right;">25.1</td>
<td style="text-align: right;">3.2</td>
<td style="text-align: right;">19.3</td>
<td style="text-align: right;">32.1</td>
<td style="text-align: right;">25.09292</td>
<td style="text-align: right;">3.137</td>
<td style="text-align: right;">19.325</td>
<td style="text-align: right;">32.043</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="even">
<td>1203</td>
<td style="text-align: right;">1229</td>
<td style="text-align: right;">12</td>
<td style="text-align: left;">01001</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Autauga County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">594.44</td>
<td style="text-align: right;">9.92547</td>
<td style="text-align: right;">-86.64273</td>
<td style="text-align: right;">32.53492</td>
<td style="text-align: left;">Exurbs</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">33860</td>
<td style="text-align: right;">1</td>
<td style="text-align: left;">Autauga</td>
<td style="text-align: left;">Montgomery, AL</td>
<td style="text-align: left;">Medium Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">2014-2018</td>
<td style="text-align: left;">DoD</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">277263</td>
<td style="text-align: right;">94</td>
<td style="text-align: right;">33.5</td>
<td style="text-align: right;">3.5</td>
<td style="text-align: right;">27.0</td>
<td style="text-align: right;">41.1</td>
<td style="text-align: right;">33.90283</td>
<td style="text-align: right;">3.497</td>
<td style="text-align: right;">27.397</td>
<td style="text-align: right;">41.489</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/08/iStock-171147284.jpg</a></td>
<td style="text-align: right;">55869</td>
<td style="text-align: right;">5499</td>
<td style="text-align: right;">67</td>
<td style="text-align: right;">9842.668</td>
<td style="text-align: right;">119.92339</td>
</tr>
<tr class="odd">
<td>1134</td>
<td style="text-align: right;">1171</td>
<td style="text-align: right;">29</td>
<td style="text-align: left;">01003</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Baldwin County</td>
<td style="text-align: left;">AL</td>
<td style="text-align: left;">01</td>
<td style="text-align: left;">Alabama</td>
<td style="text-align: right;">1589.79</td>
<td style="text-align: right;">437.47400</td>
<td style="text-align: right;">-87.72256</td>
<td style="text-align: right;">30.72748</td>
<td style="text-align: left;">Graying America</td>
<td style="text-align: left;">Metropolitan Statistical Areas</td>
<td style="text-align: right;">0</td>
<td style="text-align: left;">Underlying Cause of Death 1999-2018 - CDC Wonder</td>
<td style="text-align: left;">county</td>
<td style="text-align: left;">19300</td>
<td style="text-align: right;">2</td>
<td style="text-align: left;">Baldwin</td>
<td style="text-align: left;">Daphne-Fairhope-Foley, AL</td>
<td style="text-align: left;">Small Metro</td>
<td style="text-align: center;">Years Aggregate</td>
<td style="text-align: center;">1999-2003</td>
<td style="text-align: left;">DoD</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: left;">All</td>
<td style="text-align: right;">722311</td>
<td style="text-align: right;">215</td>
<td style="text-align: right;">29.2</td>
<td style="text-align: right;">2.0</td>
<td style="text-align: right;">25.2</td>
<td style="text-align: right;">33.1</td>
<td style="text-align: right;">29.76557</td>
<td style="text-align: right;">2.030</td>
<td style="text-align: right;">25.787</td>
<td style="text-align: right;">33.744</td>
<td style="text-align: left;"><a href="https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg" class="uri">https://www.americancommunities.org/wp-content/uploads/2018/06/iStock-891978510-2.jpg</a></td>
<td style="text-align: right;">223234</td>
<td style="text-align: right;">17627</td>
<td style="text-align: right;">217</td>
<td style="text-align: right;">7896.199</td>
<td style="text-align: right;">97.20741</td>
</tr>
</tbody>
</table>

#### Potential Uses

At this point, I believe that this dataset allows one to explore
potential correlations between historic mortality factors/rates and the
total number of deaths due to Covid-19 (in 2020), and to “drill down” on
those relationships within a given location (county or metropolitan
area). As I mentioned early, I can get a “feel” for ways to position the
data by looking at some basic summary statistics and quick
visualizations for a given bucket (i.e. “DoD” in the examples below).

    ## [1] "1691 county dod rate records for 1999-2003 (death_cause = 'DoD'): "

    ##    Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
    ##   7.096  18.065  22.654  24.199  28.167 118.688

    ## [1] "1691 county 'population' records for 1999-2003 (death_cause = 'DoD'): "

    ##    Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
    ##    3933   31263   56115  156843  130104 9611144

    ## [1] "county 'pop' summary for 2019 :"

    ##     Min.  1st Qu.   Median     Mean  3rd Qu.     Max. 
    ##     3981    32837    62317   182667   158015 10039107

    ## [1] "1747 county dod rate records for 2004-2008 (death_cause = 'DoD'): "

    ##    Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
    ##   6.428  22.353  28.055  30.032  35.296 151.404

    ## [1] "1747 county 'population' records for 2004-2008 (death_cause = 'DoD'): "

    ##    Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
    ##    3834   29403   55935  159951  135047 9750619

    ## [1] "county 'pop' summary for 2019 :"

    ##     Min.  1st Qu.   Median     Mean  3rd Qu.     Max. 
    ##     3981    30508    58988   177182   152515 10039107

    ## [1] "1753 county dod rate records for 2009-2013 (death_cause = 'DoD'): "

    ##    Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
    ##   10.67   26.58   32.70   35.07   41.02  154.61

    ## [1] "1753 county 'population' records for 2009-2013 (death_cause = 'DoD'): "

    ##    Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
    ##    3970   29495   57351  166824  142066 9894984

    ## [1] "county 'pop' summary for 2019 :"

    ##     Min.  1st Qu.   Median     Mean  3rd Qu.     Max. 
    ##     4016    30060    58722   176588   151391 10039107

    ## [1] "1779 county dod rate records for 2014-2018 (death_cause = 'DoD'): "

    ##    Min. 1st Qu.  Median    Mean 3rd Qu.    Max. 
    ##   11.88   33.80   42.22   44.81   53.24  175.74

    ## [1] "1779 county 'population' records for 2014-2018 (death_cause = 'DoD'): "

    ##     Min.  1st Qu.   Median     Mean  3rd Qu.     Max. 
    ##     2993    28815    56860   171283   144823 10138787

    ## [1] "county 'pop' summary for 2019 :"

    ##     Min.  1st Qu.   Median     Mean  3rd Qu.     Max. 
    ##     3038    29020    57602   174136   149592 10039107

*For each county record the `population` value appears to be the sum of
5 years worth of population estimates, as compared to the single year
(2019) estimate stored in `pop`.*

![](/Users/jo088ha/Projects/Currents/blogdown/sveltr/README_files/figure-markdown_strict/covid_deaths_vs_dod_rate-1.png)![](/Users/jo088ha/Projects/Currents/blogdown/sveltr/README_files/figure-markdown_strict/covid_deaths_vs_dod_rate-2.png)![](/Users/jo088ha/Projects/Currents/blogdown/sveltr/README_files/figure-markdown_strict/covid_deaths_vs_dod_rate-3.png)![](/Users/jo088ha/Projects/Currents/blogdown/sveltr/README_files/figure-markdown_strict/covid_deaths_vs_dod_rate-4.png)

What’s really intresting to me is that the extreme outliers on these
plots have relatively high rates of “Deaths of Despair” when compared to
the population, making me wonder if there is something else in the
dataset (location? demographics?) that might help to shine light on why
the rates are so high.

### Preperation for Web/Visualization

Regarding mapping and visualization of this dataset, it is possible to
take advantage of the existing store (Carto) which has already spatially
indexed the data and offers a nice SQL-like DSL to perform queries.
However, I also want to make use of the extensive search, filter and
aggregation capabilities of Elasticsearch and although ES can also index
features/records by geospatial coordinates, it’s tesselation processor
was having a hard time with this particular dataset because some
geometry values produce this error:

    {
      "error": {
        "root_cause": [
          {
            "type": "mapper_parsing_exception",
            "reason": "failed to parse field [geom] of type [geo_shape]"
          }
        ],
        "type": "mapper_parsing_exception",
        "reason": "failed to parse field [geom] of type [geo_shape]",
        "caused_by": {
          "type": "illegal_argument_exception",
          "reason": "Unable to Tessellate shape ...

To work around the error above, I have computed the bounding box and
bound it to the data as an additional column before indexing in
Elasticsearch. This way, ES can ignore the geom field, but still be
respond to spatial queries for the features in this dataset:

Scenarios
=========

### 2.1 CORI Website map

### 2.2 Broadband explorer
