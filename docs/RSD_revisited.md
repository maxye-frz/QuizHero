# Requirement Specification Document

## Problem Statement 

QuizHero is completed for the past 5 itearstions (including two grading iterations). We want to carry on this project and continue making it more like a legit web app.
The completed version of this web app will be a slimmer version of [HackMD](https://hackmd.io) with support of in-slides quizzes, slides sharing, permission control and quiz statistics.


## Potential Clients

- Instructors who want to create interactive slides online.
- Students who need to view slides and take quizzes.

## Proposed Solution

A web application.
Backend: Restful api, Javalin, postgreSQL database, pac4j...
Frontend: react, spectacle, marpit, reveal, sublime...
Deployment: docker, heroku, digital ocean, aws... 


## Functional Requirements

### Must have / Nice to have

* As an instructor, I want to login using thirdparty services including github, google drive and possibly more, so that I can manage my md files in my account.
* As an insturctor, I want to create a new md file in my account, so that I can start working on it.
* As an instructor, I want to upload a local md file to my homepage, so that I can work on the file.
* As an instructor, I want to import md files from github repo, so that I can work on the file.
* As an instructor, I want to save changes of my files, so that I can keep files updated on the server (is this only applicable to github login?).
* As an instructor, I want to save changes of my files to github/google drive or possiblly other file/code storage platforms, so that I can have the files synced and updated across the platform.
* As an instructor, I want to have permission control of my file so that I can decide to show the slides or not.
* As an instructor, I want to present my slides in html from homepage, so that I can start a presentation.
* As an instructor, I want to share my slides in a html link format from my homepage, so that I can share the link to others.
* As an instructor, I want to download my slides (in what format?) from my homepage, so that I can keep a slide-version.
* As an instructor, I want to edit my files in a real-time "collaborative" editor, so that I can edit, sync, save and update my files.
* As an instructor, I want to see my slides (vertical) from online editor interface so that I can have a preview of my slides.
* As an instructor, I want to see my slides (horizontal) from online editor interface on a separate page so that I can have a preview of my slides.
* more...


## Software Architecture 

This will be a Web-based application and will conform to the Client-Server software architecture. We need the server to store, analyze, modify and send back relevant data, and we have clients to access the server to get necessary data.



