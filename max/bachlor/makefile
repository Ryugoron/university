VERSION = 3.0.2

all:	
	pdflatex bachlor_thesis.tex
	bibtex bachlor_thesis.aux
	pdflatex bachlor_thesis.tex
	pdflatex bachlor_thesis.tex
	rm bachlor_thesis.aux
	rm bachlor_thesis.log
	rm bachlor_thesis.bbl
	rm bachlor_thesis.blg

.PHONY: clean
clean:
	rm bachlor_thesis.pdf
