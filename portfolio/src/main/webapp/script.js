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


