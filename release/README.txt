BEFORE RUNNING: 

To install/update Java:
- Download the zip file found at http://jdk.java.net/15/
- Once the file is downloaded, extract the contents to somewhere like: 
	C:\Users\<YourAccount>\OpenJDK15
where <YourAccount> is your own Windows username.

Update the Path variable:
- In the search bar, type 'Environment Variables' and an option like "Edit the
system environment variables" should come up.
- Click on "Environment Variables"
- Either edit or create a new variable called JAVA_HOME and set it to 
'C:\Users\<YourAccount>\OpenJDK15' or wherever the files extracted to.
- Click on the PATH variable and add: %JAVA_HOME%\bin to it.

After these steps, clicking the "Start Anki Helper App.bat" file should
start the app and your browser should open to "http://localhost:8017".
If it doesn't start the browser, you can type that into your browser
manually :)