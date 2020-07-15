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

/** Fetches Blobstore upload URL from the appropriate servlet. */
function fetchBlobstoreUploadURL() {
    // Fetch the URL and apply to the "action" aspect of the comment form.
    fetch("/blobstore-upload").then(promise => promise.text())
        .then((imageUploadURL) => {
          const commentForm = document.getElementById('comment-form');
          commentForm.action = imageUploadURL;
        });
}

/** 
 * Fetches stored comments from the server and display in the about UI.
 */
function getStoredComments() {
    // Make sure the user-selected number of comments is returned.
    const numComm = document.getElementById("numcom");
    urlString = "/data?num-comments=" + numComm.value;
    if (numComm.value == null) {
        urlString = "/data";
    }

    fetch(urlString).then(promise => promise.json()).then((comments) => {
        const displayList = document.getElementById("comments-container");

        // Building the displayed list of comments by iterating through the JSON object,
        // which is an array of Comment objects.
        displayList.innerHTML = '';
        for (i = 0; i < comments.length; i++) {
            curr = comments[i];
            displayList.appendChild(
                createCommentElement(curr.fname, curr.surname, curr.date, curr.message, curr.blobKey, 
                    curr.imageDescriptions, curr.descScores, curr.objectNames, curr.objectScores));

            // If not the last comment, add a horizontal line afterward.
            if (i != comments.length - 1) {
                displayList.appendChild(document.createElement("HR"));
            }
        }
    });
}

/** Deletes all the stored comments from Datastore.*/
function deleteAllComments() {
  fetch("/delete-data", {method:'POST'}).then(promise => getStoredComments());
}

/** Creates a <div> element containing a comment. */
function createCommentElement(fname, surname, date, message, blobKey, imageDescs, descScores,
    objects, objScores) {
  // Create the outer div and give it an ID.
  const commentElement = document.createElement("div");
  commentElement.id = "comment";

  // Create a paragraph element for inside the div and format the comment attributes.
  const paragraph = document.createElement("p");
  paragraph.innerHTML = 
      "<h2>" + fname + " " + surname + " | " + date + "</h2><br>" + message;
  commentElement.appendChild(paragraph);
  
  // Make sure that, if a comment has a blob, it is returned.
    
  // If there was an image included in the comment, put it under the comment.
  // Must use "" rather than isEmpty(), since imageURL may not have an isEmpty() function.
  if (!(blobKey == null || blobKey == "")) {
    // Fetch blob from GetBlobServlet.
    blobUrlString = "/get-blob?blob-key=" + blobKey;
    fetch(blobUrlString)
        .then((image) => {
          // Create a slider box element in the HTML doc using the fetched img's URL.
          const slider = document.createElement("div");
          slider.className = "slider";
          commentElement.appendChild(slider);

          // Create img elements for each slide in the HTML document using the fetched image's URL.
          const img = document.createElement("img");
          img.src = image.url;
          img.alt = "Comment picture."
          const img2 = document.createElement("img");
          img2.src = image.url;
          img2.alt = "Comment picture."

          // Create slide showing img and a table displaying image labels and confidence scores.
          sliderBox1 = document.createElement("div");  // For image labels
          sliderBox1.className = "slide";
          sliderBox1.appendChild(img);
          table = document.createElement("table");
          
          // Building header row.
          tableHeader = document.createElement("thead");
          headerLabels = document.createElement("tr");
          headerLabels.innerHTML = "<th>Image Label</th><th>Confidence</th>"
          tableHeader.appendChild(headerLabels);
          table.appendChild(tableHeader);
 
          // Building body rows. 
          tableBody = document.createElement("tbody");
          for (i = 0; i < imageDescs.length; i++) {
            row = document.createElement("tr");
            roundedScore = descScores[i].toFixed(3);
            row.innerHTML = "<td>" + imageDescs[i] + "</td><td>" + roundedScore + "</td>";
            tableBody.appendChild(row); 
          }
          table.appendChild(tableBody);
          sliderBox1.appendChild(table);


         // Create slide showing object detection info.
          sliderBox2 = document.createElement("div"); 
          sliderBox2.className = "slide";
          sliderBox2.appendChild(img2);
          table = document.createElement("table");
          
          // Building header row.
          tableHeader = document.createElement("thead");
          headerLabels = document.createElement("tr");
          headerLabels.innerHTML = "<th>Object</th><th>Confidence</th>"
          tableHeader.appendChild(headerLabels);
          table.appendChild(tableHeader);

          // If objects wasn't empty/null, then populate the body rows.
          if (!(objects == null || objects == "")) {
            // Building body rows. 
            tableBody = document.createElement("tbody");
            for (i = 0; i < objects.length; i++) {
              row = document.createElement("tr");
              roundedScore = objScores[i].toFixed(3);
              row.innerHTML = "<td>" + objects[i] + "</td><td>" + roundedScore + "</td>";
              tableBody.appendChild(row); 
            }
            table.appendChild(tableBody);
          }
          sliderBox2.appendChild(table);

          slider.appendChild(sliderBox1);
          slider.appendChild(sliderBox2);
        });
  }

  return commentElement;
}

/** Resets file input on change if file was not an image. */
function validateImage(fileInput) {
  // Acceptable image extensions.
  imgFileExtensions = [".jpg", ".jpeg", ".gif", ".png", ".bmp", ".svg", ".tiff", ".webp"];

  if (fileInput.type == "file") {
    fileName = fileInput.value;
    fNameLength = fileName.length;
    if (fNameLength > 0) {
      isImage = false;   
      fileNameLower = fileName.toLowerCase();
      // Loop thru extensions and change boolean to true if the file name equals any of them.
      for (extension of imgFileExtensions) {
        if (fileNameLower.endsWith(extension)) {
          isImage = true;
          break;
        }
      }
    }
  }
  if (!isImage) {
    alert("Invalid file type. Please submit an image.");
    fileInput.value = "";
  }
  return isImage;
}

/*
 * Adds a random quote to the about page (about.html).
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

/** 
 * Add random quote to the about page using fetch().
 */ 
function addMessageWithFetch() {
    console.log("Fetching a simple, hard-coded message.")

    // Make a request to the URL at /data; returns a promise in response.
    const promise = fetch("/data");

    // Then, pass the promise to handleResponse()
    promise.then(handleResponse);
}

/** 
 * Converts the promise to text and passes result to addMessageToDOM().
 */
function handleResponse(promise) {
    console.log("Handling the promise.");

    // The promise is a stream, not a string; here, we convert.
    const message = promise.text();
    message.then(addMessageToDOM);
}

/** 
 * Add the text from the promise to the message container in about.html.
 */
 function addMessageToDOM(msg) {
    console.log("Setting message on about page.");

    // Find the appropriate container in the document and display message.
    const container = document.getElementById("data-container");
    container.innerText = msg;
}

/**
 * Practice with using arrow functions; should work exactly the same way as past 3 functions together.
 */
function addMessageUsingArrowFunctions() {
    console.log("Setting message using arrow functions.");

    /* Promise chain: first, JS makes a request to the /data URL.
    The resulting promise is then converted to text. That text value is then 
    returned/passed as a parameter to an anonymous func that finds data 
    container in the doc and sets the message.
    */
    fetch("/data").then(promise => promise.text()).then((msg) => 
    document.getElementById("data-container").innerText = msg);
}

/**
 * Week 3 Step 3: Fetch JSON.
 */
function addCommentsUsingArrowFunctions() {
    console.log("Setting message using arrow functions.");

    /* Promise chain: first, JS makes a request to the /data URL.
    The resulting promise is then converted to JSON. That JSON value is then 
    returned/passed as a parameter to an anonymous func that finds data 
    container in the doc and sets the message.
    */
    fetch("/data").then(promise => promise.json()).then((msgs) => {
        const displayList = document.getElementById("comments-container");
        displayList.innerHTML = '';
        displayList.appendChild(
            createListElement("Message 1: "+ msgs.message0));
        displayList.appendChild(
            createListElement("Message 2: "+ msgs.message1));
        displayList.appendChild(
            createListElement("Message 3: "+ msgs.message2));
    });
}

/** Creates an <li> element containing text. (From examples/server-stats). */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}
