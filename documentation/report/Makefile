tex = report.tex
report = $(patsubst %.tex, %.pdf, $(tex))
dots = $(wildcard dot/*.dot)
dias = $(wildcard dia/*.dia)
texs = $(wildcard files/*.tex) $(wildcard files/appendix/*.tex) title.tex
schems = $(wildcard img/schematics/*pdf)
imgs = $(wildcard img/*.pdf)

all : $(report)

$(report) : $(tex) $(dot_pdfs) $(dia_pngs) $(texs) $(schems) $(imgs)
	pdflatex $<
	bibtex $(basename $<)
	pdflatex $<
	#makeglossaries $(basename $<)
	pdflatex $<

clean :
	rm $(report) $(dot_pdfs) $(dia_pngs)
