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

/**
 * Adds a random greeting to the page.
 */
function addRandomFunFact() {
  const fun_facts =
      ['Born in June', 'Exposed to coding at age 13', 'Enjoys Poetry', 'Aspiring to be a fluent French speaker', 'Speaks two and a half languages :)', 
      'Body was not designed to float in water', 'Have had the same haircut style since third grade', 'Once wanted to be a lawyer', 
      'Probably the only person named "Zintan" in Google! maybe ;)'];

  // Pick a random fun_fact.
  const fun_fact = fun_facts[Math.floor(Math.random() * fun_facts.length)];

  // Add it to the page.
  const fun_factContainer = document.getElementById('fun-fact-container');
  fun_factContainer.innerText = fun_fact;
}

// Adds a random image to the page
function addRandomImage(){
    const imgIndex = Math.floor(Math.random() * 7);
    const imgUrl = "/images/zintan-" + imgIndex + ".jpg";
    
    const imgElement = document.createElement('img');
    imgElement.src = imgUrl;

    const imageContainer = document.getElementById('image-container');
    imageContainer.innerHTML = ''; //remove previous image
    imageContainer.appendChild(imgElement);

}
