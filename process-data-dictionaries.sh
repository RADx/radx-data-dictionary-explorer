if [ $# -eq 0 ]
  then
    echo "Two arguments must be supplied: (1) The input directory containing data dictionaries, (2) The output directory."
	exit 1
fi

echo "Processing data dictionaries in $1 and sending output to $2"

curated=$1
outdir=$2
processingdir=$outdir/processing
splitdictionaries=$outdir/splitdictionaries
latestversions=$outdir/latestversions
withsources=$outdir/withsources
withdigests=$outdir/withdigests


mkdir $outdir

dd make-csv --in $curated --out $outdir/0-extracted

dd strip --in $outdir/0-extracted --out $outdir/1-stripped              

dd collapse --in  $outdir/1-stripped --out $outdir/2-collapsed --key-field-name question_name --key-field-regex "^([^\-]+)\-\d+" --key-field-regex-group 1

dd fill-down --in $outdir/2-collapsed --out $outdir/3-filled --field-names survey_name,survey_version,"Section Header"    

dd replace-field-names --in $outdir/3-filled --out $outdir/4-mapped           

dd transform --in $outdir/4-mapped --out $outdir/5-normalized --lowercase --collapse-white-space --field-names required,variable_name,datatype,units 

dd split --in $outdir/5-normalized --out $outdir/6-split  --field-names survey_name  

dd retain-max-rows --in $outdir/6-split --out $outdir/7-deduped --field-names survey_version

dd filter --in $outdir/7-deduped --out $outdir/8-non-blank-variable-names --field-value-filter variable_name=.+

dd append-source --in $outdir/8-non-blank-variable-names --out $outdir/9-with-sources  

dd append-digest --in $outdir/9-with-sources   --out $outdir/10-with-digests --field-names variable_name,source_file,source_directory

dd merge --in $outdir/10-with-digests --out $outdir/merged.csv  --distinct --sorted



# dd retain-fields --in $latestversions --out $outdir/variable_names --field-names variable_name,label
# dd distinct --in $outidr/variable_names --out $outidr/variable_names
# dd append-source --in $outdir/variable_names --out $outdir/variable_names
# dd merge --in $outdir/variable_names --out $outdir/variable_names-summary.csv --sorted
#
#
# dd retain-fields --in $withsources --out $outdir/datatypes --field-names datatype,source_file,source_directory
# dd distinct --in $outdir/datatypes --out $outdir/datatypes
# dd merge --in $outdir/datatypes --out $outdir/datatypes-summary.csv  --distinct --sorted
#
# dd retain-fields --in $latestversions --out $outdir/units --field-names units,source_file,source_directory
# dd distinct --in $outdir/units --out $outdir/units
# dd merge --in $outdir/units --out $outdir/units-summary.csv  --distinct --sorted
#
# dd retain-fields --in $latestversions --out $outdir/labels --field-names label,source_file,source_directory
# dd merge --in $outdir/labels --out $outdir/labels-summary.csv  --distinct --sorted
#
# dd retain-fields --in $latestversions --out $outdir/value-constraints --field-names value_constraints,source_directory
# dd merge --in $outdir/value-constraints --out $outdir/value-constraints-summary.csv  --distinct --sorted