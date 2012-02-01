tex = report.tex
report = $(patsubst %.tex, %.pdf, $(tex))
dots = $(wildcard dot/*.dot)
dias = $(wildcard dia/*.dia)
texs = $(wildcard files/*.tex) $(wildcard files/appendix/*.tex) title.tex
schems = $(wildcard img/schematics/*pdf)
imgs = $(wildcard img/*.pdf)

dot_pdfs = $(patsubst dot/%.dot, img/%.pdf, $(dots))
dia_pngs = $(patsubst dia/%.dia, img/%.png, $(dias))

img/%.pdf : dot/%.dot
	dot -Tpdf $< -o $@

img/%.png : dia/%.dia
	dia -e $@ $<

all : $(report)

$(report) : $(tex) $(dot_pdfs) $(dia_pngs) $(texs) $(schems) $(imgs)
	pdflatex $<
	bibtex $(basename $<)
	pdflatex $<
	#makeglossaries $(basename $<)
	pdflatex $<

clean :
	rm $(report) $(dot_pdfs) $(dia_pngs)
