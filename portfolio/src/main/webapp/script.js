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
 * Adds a random quote to the page.
 */
function addRandomQuote() {
  const quotes =
      ['\"I found I could say things with color and shapes that I couldn\'t say any other \
      way...things I had no words for.\"', 
      '\"When you look back at where you have been, it oftens seems as if you have never been \
      there or even as if there were no such place.\"', 
      '\"All colours are the friends of their neighbors, and the lovers of their opposites.\"', 
      '\"We cross our bridges as we come to them and burn them behind us, with nothing to show \
      for our progress except a memory of the smell of smoke, and the presumption that once our eyes watered.\"', 
      '\"Beauty plus pity--that is the closest we can get to a definition of art. Where there is beauty there \
      is pity for the simple reason that beauty must die: beauty always dies, the manner dies with the matter, \
      the world dies with the individual.\"', "\"Cyberspace. A consensual hallucination experienced daily by \
      billions of legitimate operators, in every nation, by children being taught mathematical concepts... \
      A graphic representation of data abstracted from banks of every computer in the human system. \
      Unthinkable complexity. Lines of light ranged in the nonspace of the mind, clusters and constellations \
      of data. Like city lights, receding...\"", "\"Dance,\" said the Sheep Man. \"Yougottadance. \
      Aslongasthemusicplays. Yougotta dance. Don'teventhinkwhy. Starttothink, yourfeetstop. Yourfeetstop, \
      wegetstuck. Wegetstuck, you'restuck. Sodon'tpayanymind, nomatterhowdumb. Yougottakeepthestep. \
      Yougottalimberup. Yougottaloosenwhatyoubolteddown. Yougottauseallyougot. Weknowyou're tired, tiredandscared. \
      Happenstoeveryone, okay? Justdon'tletyourfeetstop.\""];

  // Pick a random quote.
  const quote = quotes[Math.floor(Math.random() * quotes.length)];

  // Test console print.
  console.log("success displaying quote.");

  // Add it to the page.
  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;
}
