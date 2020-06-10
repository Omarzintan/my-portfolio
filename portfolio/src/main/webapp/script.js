// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// Global Variables
var currentIndex = -1;
var deleteAllEventNotAdded = true;
/**
 * Adds a random greeting to the page.
 */
function addRandomFunFact() {
  const fun_facts =
    ['"I am a June born."',
     '"I was exposed to coding at age 13."',
     '"I enjoy Poetry"',
     '"I aspire to be a fluent French speaker."',
     '"I speak two and a half languages :)"', 
     '"I cannot float in water."',
     '"I have had the same haircut since the third grade."',
     '"I once wanted to be a lawyer."', 
     '"I am the only one of my siblings to have a middle name."',
     '"I enjoy listening to hip-hop, gospel and jazz music."',
     '"I am left-handed."',
     '"I would like to tour Venice in the future."'
    ];

  // Pick a random fun_fact.
  var index = Math.floor(Math.random() * fun_facts.length);
  while (index == currentIndex) {
      index = Math.floor(Math.random() * fun_facts.length);
  }
  const fun_fact = fun_facts[index];
  currentIndex = index;
  // Add it to the page.
  const fun_factContainer = document.getElementById('fun-fact-container');
  fun_factContainer.innerText = fun_fact;
}


/* Fetches comments from server and sends them to index.html */
function commentCollector() {
  fetch('/data')
  .then(response => response.json())
  .then((commentList) => {
    var commentListLength = commentList.length;
    var commentListDisplayLength = document.getElementById('number-comments').value ;
    if (commentList == null || commentList.length == 0) {
      return;
    }
    const commentListElement = document.getElementById('comment-list');
    if (deleteAllEventNotAdded) {
      const deleteButtonElement = document.getElementById('delete-all-button');
      deleteButtonElement.addEventListener('click', () => {
        deleteComments();
        commentListElement.remove();
        });
    }
    commentListElement.innerHTML = '';
    if (commentListLength >= commentListDisplayLength) {
      for (i = 0; i < commentListDisplayLength; i++ ) {
        commentListElement.appendChild(
          createComment(commentList[i])
          );
      }
    }
    else {
      for (i = 0; i < commentListLength; i++ ) {
        commentListElement.appendChild(
          createComment(commentList[i])
          );
      }
    }
  });
}

/* Deletes all comments */
function deleteComments() {
  fetch('/delete-data', {
    method: 'POST',
    });
}

function deleteOneComment(comment) {
  const params = new URLSearchParams();
  params.append('id', comment.id);
  fetch('/delete-one-comment', {
    method: 'POST', body: params
  });
}

/** Retrieves user login status and login and logout urls */
function getUserLoginStatus() {
  fetch('/login')
  .then(response => response.json())
  .then((responseList) => {
    if (responseList == null || responseList.length == 0) { 
      return;
    } 
    var loginStatus = responseList[0];
    var loginUrl = responseList[1];
    var logoutUrl = responseList[2];
    var userEmail = responseList[3];
    const commentsDivElement = document.getElementById('comments');
    const loginDivElement = document.getElementById('login');
    const loginLinkElement = document.createElement('a');
    const logoutLinkElement = document.createElement('a');
    loginLinkElement.innerText = 'Login';
    logoutLinkElement.innerText = 'Logout';
    loginLinkElement.href=loginUrl;
    logoutLinkElement.href = logoutUrl;
    loginLinkElement.className = 'external-links';
    logoutLinkElement.className = 'external-links';

    if (loginStatus == '0') {
      commentsDivElement.style.display = 'none';
      loginDivElement.appendChild(loginLinkElement);
    }
    else { 
      textElement = document.createElement('p');
      textElement.innerHTML = 'Hey '+userEmail+', you can comment below';
      loginDivElement.appendChild(textElement);
      loginDivElement.appendChild(logoutLinkElement);
      commentCollector() }
    });   
}

/** Creates HTML tags for comment post */
function createComment(comment) {
  var username = comment.username;
  var userComment = comment.text;
  var date = new Date(comment.timestamp);
  var dateString = date.toDateString();
  var timeString = date.toLocaleTimeString();
  const divElement = document.createElement('div');
  const imgUrl = 'images/avatar.jpg';
  const imgElement = document.createElement('img');
  const spanElement = document.createElement('span');
  const paragraphElement = document.createElement('p');
  const textElement = document.createElement('p');
  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.className = 'button';
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteOneComment(comment);
    divElement.remove();
  });
  imgElement.src = imgUrl;
  spanElement.innerHTML = username;
  paragraphElement.appendChild(spanElement);
  spanElement.after(' ' + timeString + ' ' + dateString);
  textElement.innerText = userComment;
  divElement.appendChild(imgElement)
  divElement.appendChild(paragraphElement);
  divElement.appendChild(textElement);
  divElement.appendChild(deleteButtonElement);
  divElement.className = 'comment-box';
  return divElement;
}
