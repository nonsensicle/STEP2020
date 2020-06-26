/**
 * Modified lightbox.js script for four different lightboxes on one page (about.html)
 */

// Display the box
function openBox() {
    document.getElementById("lightbox").style.display = "block";
}

// Close the box
function closeBox() {
    document.getElementById("lightbox").style.display = "none";
}

var whichImg = 0;
showImg(whichImg);

// Display a particular image in the lightbox 
// place code (0 = korea, 1 = hanover, 2 = Italy, 3 = athens)
function showImg(n, place) {

    var placeString = '';   // Hold ID of the place based on place code
    switch(place) {
        case 0:
            placeString = "korea";
            break;
        case 1:
            placeString = "hanover";
            break;
        case 2:
            placeString = "italy";
            break;
        case 3:
            placeString = "athens";
            break;
        default:
            break;
    }

    var imgs = document.getElementsByClassName(placeString);  // the class holds an array of html divs
    var captionTxt = document.getElementById("caption");

    // Wrap around if at beginning or end
    if (n > imgs.length) {
        whichImg = 0;
    }
    if (n < 0) {
        whichImg = imgs.length - 1;
    }
  
    // Hide all images
    for (var i = 0; i < imgs.length; i++){
        imgs[i].style.display = "none";   
    }

    // Display current image 
    imgs[n].style.display = "block";

    // Change caption to alt text by getting array of images inside the div "imgs"
    captionTxt.innerHTML = imgs[n].getElementsByTagName('img')[0].alt;
}

// Next/previous; expected input 1 or -1 for n, place code for place
function slideMod(n, place) {
    showImg(whichImg += n, place);
}

// Onclick for thumbnails; input: index & place code (0 = korea, 1 = hanover, 2 = Italy, 3 = athens)
function currentImg(n, place) {
    showImg(whichImg = n, place);
}
