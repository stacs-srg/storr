echo startTime: 
date +"%H:%M:%S"

java -d64 -Xmx24g -cp target/record_classification-1.0-SNAPSHOT-jar-with-dependencies.jar uk.ac.standrews.cs.digitising_scotland.parser.pipeline.TrainAndMultiplyClassify FakeKilmCodes/kilmWithFakeCodesPipe.txt FakeKilmCodes/kilmWithFakeCodesPipe.txt

echo finishTime: 
date +"%H:%M:%S"

for d in Experiment[0-9][0-9]
do
    ( cd $d/Reports && markdown summary.md )
done
