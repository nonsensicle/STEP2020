/**
 * Modified lightbox.js script for four different lightboxes on one page (about.html).
 */

// Display the box.
// place code (0 = korea, 1 = hanover, 2 = Italy, 3 = athens)
function openBox(place) {
    var placeString = '';   // Hold ID of the place based on place code
    switch(place) {
        case 0:
            placeString = "lightkor";
            break;
        case 1:
            placeString = "lighthan";
            break;
        case 2:
            placeString = "lightit";
            break;
        case 3:
            placeString = "lightat";
            break;
        default:
            break;
    }
    document.getElementById(placeString).style.display = "block";
}

// Close the box.
function closeBox(place) {
    var placeString = '';   // Hold ID of the place based on place code
    switch(place) {
        case 0:
            placeString = "lightkor";
            break;
        case 1:
            placeString = "lighthan";
            break;
        case 2:
            placeString = "lightit";
            break;
        case 3:
            placeString = "lightat";
            break;
        default:
            break;
    }
    document.getElementById(placeString).style.display = "none";
}

var whichImg = 0;
showImgMod(whichImg);

// Display a particular image in the lightbox.
function showImgMod(n, place) {

    var placeString = '';   // Hold ID of the place based on place code
    var captionString = ''; // Hold ID of caption 
    switch(place) {
        case 0:
            placeString = "korea";
            captionString = "caption0";
            break;
        case 1:
            placeString = "hanover";
            captionString = "caption1";
            break;
        case 2:
            placeString = "italy";
            captionString = "caption2";
            break;
        case 3:
            placeString = "athens";
            captionString = "caption3";
            break;
        default:
            break;
    }

    var imgs = document.getElementsByClassName(placeString);  // The class holds an array of html divs
    var captionTxt = document.getElementById(captionString);

    // Wrap around if at beginning or end.
    if (n > (imgs.length - 1)) {
        whichImg = 0;
    }
    if (n < 0) {
        whichImg = imgs.length - 1;
    }
  
    // Hide all images.
    for (var i = 0; i < imgs.length; i++){
        imgs[i].style.display = "none";   
    }

    // Display current image.
    imgs[whichImg].style.display = "block";

    // Change caption to alt text by getting array of images inside the div "imgs".
    captionTxt.innerHTML = imgs[whichImg].getElementsByTagName('img')[0].alt;
}

// Next/previous; expected input 1 or -1 for n, place code for place.
function slideMod(n, place) {
    showImgMod((whichImg += n), place);
}

// Onclick for thumbnails; input: index & place code (0 = korea, 1 = hanover, 2 = Italy, 3 = athens).
function currentImg(n, place) {
    showImgMod((whichImg = n), place);
}
