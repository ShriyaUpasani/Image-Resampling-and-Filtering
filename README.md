# Image Resampling and Filtering

This project aims to provide a practical understanding of resampling and filtering and their effects on visual media types like images. The program reads an image in RGB format, resamples it, and optionally applies anti-aliasing. It also allows viewing the original image details by overlaying a window when the control key is pressed.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Examples](#examples)
- [Implementation Details](#implementation-details)
  - [Resampling](#resampling)
  - [Anti-aliasing](#anti-aliasing)
  - [Detail Overlay](#detail-overlay)
- [Working](#working)  


## Overview

This program resamples a given image and optionally applies an anti-aliasing filter to reduce aliasing artifacts. It also allows users to view original image details by overlaying a detailed view around the mouse cursor when the control key is pressed.

## Features

- Resampling of images by a specified scale factor.
- Optional anti-aliasing using an averaging low-pass filter.
- Detail overlay feature to view original image details around the mouse cursor.
- Written in a non-scriptable language (C/C++ or Java), without using external libraries.

## Requirements

- A Java compiler.
- A system capable of displaying images.

## Installation

- Clone the repository: git clone https://github.com/yourusername/image-resampling-filtering.git
- cd image-resampling-filtering

## Usage
YourProgram.exe C:/myDir/myImage.rgb S A w

C:/myDir/myImage.rgb: The input image file in RGB format.
S: A floating-point value between 0 and 1 indicating the resampling scale factor.
A: A Boolean value (0 or 1) indicating whether to apply anti-aliasing.
w: The width and height of the square window for the detailed overlay.

## Examples
javac ImageDisplay.java; java ImageDisplay "aliasing_test_data_samples\aliasing_test1.rgb" 0.08 0 200   //AntiAliasing example
javac ImageDisplay.java; java ImageDisplay .\16xHD_data_samples\national-park.rgb 0.125 1 200  //Detail Overlay and scaling

## Implementation Details
#### Resampling
The program rescales the image by the factor S provided as input. This reduces the image dimensions by the specified scale, fitting it into a smaller window for display.

#### Anti-aliasing
When A is set to 1, an averaging low-pass filter is applied to the image to reduce aliasing artifacts. This smooths the image and improves visual quality when downscaled.

#### Detail Overlay
When the control key is pressed, the program overlays a detailed view of the original image around the mouse cursor in a w x w window. This allows users to see the fine details of the image while browsing.

## Working

#### Detail overlay
![gif-a1-overlay](https://github.com/user-attachments/assets/66cf32dc-86de-4698-93e8-4cbf87ebd7ed)

#### Anti-aliasing
##### Aliasing off
![image](https://github.com/user-attachments/assets/e5f5f330-33e7-4f49-8089-a9545b9d2b91)

##### Aliasing on
![image](https://github.com/user-attachments/assets/5dd27085-db20-4c2d-a8f1-e71d89c1a29e)

