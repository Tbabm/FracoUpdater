# FracoUpdater
An offline wrapper of [Fraco](http://www.cs.mcgill.ca/~swevo/Fraco), implemented for rule-based Just-In-Time comment updating.

## Usage
```bash
# install RefactoringMiner
git clone https://github.com/tsantalis/RefactoringMiner.git
git checkout 579669ac
./gradlew install

# compile
mvn compile

# run
# TEST_SET is the path to test set
# OUT_FILE is the file to store results
mvn exec:java -Dexec.args="TEST_SET OUT_FILE"
```
