# Installation #

  * The repository url can be found on the website under Source>Checkout. It will look something like: https://username@code.google.com/p/project-name/
  * When I refer to your password, I am not talking about the password you use to log into your Google account. I am referring to the one you can find here: https://code.google.com/hosting/settings
  * Mercurial is the version control system we will use. It lets us work as a group on the code. It is similar to SVN.
  * Mercurial and hg are synonyms

## Eclipse ##
  * [Install Mercurial](http://ekkescorner.wordpress.com/blog-series/git-mercurial/step-by-step-install-mercurial-on-osx-ubuntu-and-windows/)
  * Install Eclipse
  * Install MercurialEclipse
    * Open Eclipse AS ROOT or as AN ADMINISTRATOR
      * When it asks you for a workspace, make a new workspace somewhere that you will never use
      * That way you don't make a bunch of files in the workspace that your non-admin account can't access
    * Navigate to Help>Install New Software....
    * Click Add
      * Name: HG
      * Location: http://mercurialeclipse.eclipselabs.org.codespot.com/hg.wiki/update_site/stable/
    * Tick off MercurialEclipse
    * Keep clicking Next, Install Unsigned Content, yada yada yada, restart
  * Import the Project
    * In white area in the Package Explorer on the left,
      * Right Click > Import > Mercurial > Clone Existing Mercurial Repository>Next
      * Input the following info:
      * URL: The repository URL
      * Username: Your gmail email address (ie username@gmail.com)
      * Password: Your password
      * Checkout as a project(s) in the workspace: Make sure this is ticked
      * Next>Next>Finish

# Use Instructions #

## General ##
  * Revision: A save point of all the code at a specific point in time.
    * In SVN they were numbered (1, 2, 3....)
    * In HG they have names like 3df19fc50e26. You identify them by what you wrote for the commit log (important!)
    * Commit: This saves all your code on your computer so you can go back to that revision at any time. It does NOT share it with the public yet, just adds it to a log for yourself.
    * Push: Sends all your changes to the main server that we all work off of
    * Pull: Pulls in any new code that other people wrote
    * Merge: Takes two conflicting revisions and (tries to) mash them together. Sometimes you will have to fix stuff manually.


# See Also #
  * Transition from svn: http://hginit.com/00.html
  * In general: http://hginit.com/index.html