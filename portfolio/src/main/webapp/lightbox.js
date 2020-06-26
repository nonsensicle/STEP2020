/**
 * Adding a lightbox feature to the art gallery pages (prints.html, illustrations.html, doodles.html) 
 * Referred to https://www.w3schools.com/howto/howto_js_lightbox.asp
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
function showImg(n) {
    var imgs = document.getElementsByClassName("imgs");  // "imgs" is an array of html divs
    var captionTxt = document.getElementById("caption");

    // Wrap around if at beginning or end
    if (n > (imgs.length - 1)) {
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
    imgs[whichImg].style.display = "block";

    // Change caption to alt text by getting array of images inside the div "imgs"
    captionTxt.innerHTML = imgs[whichImg].getElementsByTagName('img')[0].alt;
}

// Next/previous (expected input 1 or -1)
function slide(n) {
    showImg(whichImg += n);
}

// Onclick for thumbnails; input: index
function currentImg(n) {
    showImg(whichImg = n);
}
