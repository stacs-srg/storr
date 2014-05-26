
for i in src/test/resources/*test.dot
do
dot -Tpdf $i > $i.pdf
mkdir -p target/diagrams
cp $i.pdf target/diagrams
done
