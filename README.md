# DropletAndBubbleImageProcessingImageJ
Java code using ImageJ filters to count bubbles and droplets  and to write time-dependent data

It can be useful to reveal data regarding the centroid of a single bubble study. In addition, it can be used to count bubbles and droplets in experimental studies in a bubble column. ImageJ filters and packages are used. However, it cannot be used as a an ImageJ plugIn yet. I am trying to convert this a plugin. 

You can compile the code with compile scripts easily.
./compile
When you run the code, first it asks you the directory and the file. It is designed to get the stack of images in the folder you have chosen. It sorts numerically the filenames so that they can be in increasing order. 

To test the code, I captured an image from the article below, and then I copied the same image as if each image represents every time. It is worth noting that the images to be processed should be in gray scale. It generates the outlines of counting and csv files regarding the label of every bubble or droplet tracked. However, the particle analyzer detects two bubbles in contact to each other, even if it is a single bubble. This may be attributed to thresholding. If you are studying to extract the centroid of a bubble, you may take average centroid of these two detected particles. 



Mirsandi, H., Smit, W. J., Kong, G., Baltussen, M. W., Peters, E. A. J. F., & Kuipers, J. A. M. (2020). Influence of wetting conditions on bubble formation from a submerged orifice. Experiments in Fluids, 61(3), 1–18. https://doi.org/10.1007/s00348-020-2919-7

