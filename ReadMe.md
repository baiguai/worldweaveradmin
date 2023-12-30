[Clone]
git clone https://USER@bitbucket.org/REPO.git

[Add Remote]
git remote add origin https://github.com/REPO.git
git push -u origin master

[On Local Machine]
cd into project root dir
echo "# My project's README" >> README.md git
add README.md
git commit -m "Initial commit"
git push -u origin master

[Log aliases]
log --oneline --graph --summary --decorate --max-count=10
git log --graph --oneline --decorate


[Credentials]
git config credential.helper store



[Typical Development Cycle Example]

Navigate to the project root:

$~ cd /home/myuser/mygame/


Create a new effort / feature branch and activate
that branch

$~ git checkout -b NewFeature


Edit project files, create new ones etc.
Commit the changes:

$~ git add *
$~ git commit
(Add comments in the editor that pops up and save, close)


Switch to the the master branch (don't forget your
branch's name, otherwise you'll have to pull up all the
branches and find it)

$~ git checkout master


Merge the new branch into the master

$~ git merge NewFeature


Now that it has been merged into the master branch
we can push it.

$~ git push






// Building and running
[root] sh _compileapp.sh

[root]/Compiled sh _compilejar.sh

[root]/Release sh _launchapp.sh

-- Windows --
[root] javac -cp .:Release/lib/sqlitejdbc.jar -d ./Compiled ./Code/*.java

[root]/Compiled jar -cfm ../Release/WorldWeaver.jar manifest.txt *.class

[root]/Release java -jar WorldWeaver.jar EscapeApsis


// Building and Copying Game
- Launch app, enter:  buildgame EscapeApsis, Apsis

Copy [root]/Release/Data/Games/EscapeApsis to WorldWeaver/Release/Data/Games/EscapeApsis

