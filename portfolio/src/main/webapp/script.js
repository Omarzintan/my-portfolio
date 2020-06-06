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
var currentIndex=-1;
var deleteAllEventNotAdded = true;
/**
 * Adds a random greeting to the page.
 */
function addRandomFunFact() {
  const fun_facts =
      ['"I am a June born!"',
       '"I was exposed to coding at age 13!"',
       '"I enjoy Poetry!"', '"I aspire to be a fluent French speaker!"',
       '"I speak two and a half languages :)"', 
       '"I cannot float in water!"',
       '"I have had the same haircut since the third grade!"',
       '"I once wanted to be a lawyer!"', 
       '"I am the only one of my siblings to have a middle name!"',
       '"I enjoy listening to hip-hop, gospel and jazz music!"',
       '"I am left-handed!"',
       '"I would like to tour Venice in the future!"'
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


/* fetches comments from server and sends them to index.html */
function commentCollector() {
    fetch('/data')
    .then(response => response.json())
    .then((commentList) => {
      const commentListElement = document.getElementById('comment-list');
      if (deleteAllEventNotAdded) {
        const deleteButtonElement = document.getElementById('delete-all-button');
        deleteButtonElement.addEventListener('click', () => {
          deleteComments();
          commentListElement.remove();
      });
      }
      commentListElement.innerHTML = "";
      var commentListDisplayLength = document.getElementById('number-comments').value ;
      var commentListLength = commentList.length;
      if (commentListLength != 0 && commentListLength >= commentListDisplayLength) {
        for (i = 0; i < commentListDisplayLength; i++ ) {
        commentListElement.appendChild(
          createComment(commentList[i])
          );
        }
      }
      else if (commentListLength > 0) {
        for (i = 0; i < commentListLength; i++ ) {
        commentListElement.appendChild(
          createComment(commentList[i])
          );
        }
      }
      
    });
}

/* deletes all comments */
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

/** Creates HTML tags for comment post */
function createComment(comment) {
  var username = comment.username;
  var userComment = comment.text;
  var date = new Date(comment.timestamp);
  const divElement = document.createElement('div');
  const imgUrl = 'images/avatar.jpg';
  const imgElement = document.createElement('img');
  const spanElement = document.createElement('span');
  const paragraphElement = document.createElement('p');
  const textElement = document.createElement('p');
  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.className = "comment-delete-button";
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteOneComment(comment);
    divElement.remove();
  });
  imgElement.src = imgUrl;
  spanElement.innerHTML = username;
  paragraphElement.appendChild(spanElement);
  spanElement.after(" " + date.toDateString());
  textElement.innerText = userComment;
  divElement.appendChild(imgElement)
  divElement.appendChild(paragraphElement);
  divElement.appendChild(textElement);
  divElement.appendChild(deleteButtonElement);
  divElement.className = "comment-box";
  console.log(divElement);
  return divElement;
}
