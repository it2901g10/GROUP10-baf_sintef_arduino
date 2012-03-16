tex = report.tex
report = $(patsubst %.tex, %.pdf, $(tex))
texs = $(wildcard files/*.tex) $(wildcard files/appendix/*.tex) $(wildcard files/appendix/meetingminutes/*.tex) $(wildcard files/appendix/weekreports/*.tex) title.tex
imgs = $(wildcard img/*.pdf)

all : $(report)

$(report) : $(tex) $(texs) $(imgs)
	pdflatex $<
	#bibtex $(basename $<)
	pdflatex $<
	makeglossaries $(basename $<)
	pdflatex $<

clean :
	rm $(report)
