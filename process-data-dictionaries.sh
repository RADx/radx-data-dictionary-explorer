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

dd make-csv --in $curated --out $processingdir

dd strip --in $processingdir --out $processingdir                

dd collapse --in  $processingdir --out $processingdir --key-field-name question_name --key-field-regex "^([^\-]+)\-\d+" --key-field-regex-group 1

dd fill-down --in  $processingdir --out $processingdir --field-names survey_name,survey_version,"Section Header"    

dd replace-field-names --in $processingdir --out $processingdir           

dd transform --in $processingdir --out $processingdir --lowercase --collapse-white-space --field-names required,variable_name,datatype,units 

dd split --in $processingdir --out $splitdictionaries  --field-names survey_name  

dd retain-max-rows --in $splitdictionaries --out $latestversions --field-names survey_version

dd append-source --in $latestversions --out $withsources  

dd append-digest --in $withsources   --out $withdigests --field-names variable_name,source_file,source_directory

dd merge --in $withdigests --out $outdir/merged.csv  --distinct --sorted



dd retain-fields --in $latestversions --out $outdir/variable_names --field-names variable_name,label
dd distinct --in $outidr/variable_names --out $outidr/variable_names
dd append-source --in $outdir/variable_names --out $outdir/variable_names
dd merge --in $outdir/variable_names --out $outdir/variable_names-summary.csv --sorted


dd retain-fields --in $withsources --out $outdir/datatypes --field-names datatype,source_file,source_directory
dd distinct --in $outdir/datatypes --out $outdir/datatypes
dd merge --in $outdir/datatypes --out $outdir/datatypes-summary.csv  --distinct --sorted

dd retain-fields --in $latestversions --out $outdir/units --field-names units,source_file,source_directory
dd distinct --in $outdir/units --out $outdir/units
dd merge --in $outdir/units --out $outdir/units-summary.csv  --distinct --sorted

dd retain-fields --in $latestversions --out $outdir/labels --field-names label,source_file,source_directory
dd merge --in $outdir/labels --out $outdir/labels-summary.csv  --distinct --sorted

dd retain-fields --in $latestversions --out $outdir/value-constraints --field-names value_constraints,source_directory
dd merge --in $outdir/value-constraints --out $outdir/value-constraints-summary.csv  --distinct --sorted