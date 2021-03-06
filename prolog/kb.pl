:- module( kb, [ stessa_frase/2
               , writeKB/0
               , expandKB/0
               , explainKB/0
               , resultKB/0
               , assertFact/1
               , assertTag/4
               , assertTag/5
               , assertTag/6
               , nextIDTag/1
               , nextIDToken/1
               , nextIDDocument/1
               , lastIDTag/1
               , lastIDToken/1
               , lastIDDocument/1
               , assertDoc/1
               , assertDocs/1
               , vicini/2
               , spiegaTutto/2
               ]
).

:- use_module(library(lists)).
:- use_module(lexer).

:- dynamic(kb:depends/2).
:- dynamic(kb:next/2).
:- dynamic(kb:token/2).
:- dynamic(kb:tag/2).
:- dynamic(kb:spiega/2).
:- dynamic(kb:vuole/1).
:- dynamic(kb:fatto/1).
:- dynamic(kb:documento/2).
:- dynamic(kb:appartiene/2).
:- dynamic(kb:lastIDTag/1).
:- dynamic(kb:lastIDToken/1).
:- dynamic(kb:lastIDDocument/1).
:- dynamic(kb:val/2).

assertDoc( Documento ) :-
    to_string(Documento, Stringa),
    nextIDDocument(ID),
    assertFact(kb:documento(ID, Stringa)).

assertDocs([]).
assertDocs([X|Xs]) :-
    to_string(X, Stringa),
    nextIDDocument(ID),
    assertFact(kb:documento(ID, Stringa)),
    assertDocs(Xs).

writeKB :-
    findall((IDDoc, Doc), kb:documento(IDDoc, Doc), ListaDoc),
    forall(member((IDDoc,Doc), ListaDoc), (
        write(IDDoc),nl,
        write(IDDoc),write(': lexer...'),flush_output,
        lexer(Doc, ListaParole),
        write(' DONE'),nl,flush_output,
        nextIDToken(IDTokBOF),
        atom_concat(IDDoc, '_BOF', NomeToken),
        assertFact(kb:token(IDTokBOF, NomeToken)),
        assertFact(kb:appartiene(IDTokBOF, IDDoc)),        
        write(IDDoc),write(': tokenizer...'),flush_output,
        tokenizer(IDDoc, ListaParole, IDTokBOF),
        write(' DONE'),nl,flush_output
    ) ).

tokenizer( IDDoc, [ Parola | [] ], IDTokenPrecedente ) :-
    nextIDToken(IDTok),
    assertFact(kb:token(IDTok, Parola)),
    assertFact(kb:appartiene(IDTok, IDDoc)),
    assertFact(kb:next(IDTokenPrecedente, IDTok)),

    nextIDToken(IDTokEOF),
    atom_concat(IDDoc, '_EOF', EndToken),
    assertFact(kb:token(IDTokEOF, EndToken)),
    assertFact(kb:appartiene(IDTokEOF, IDDoc)),
    assertFact(kb:next(IDTok, IDTokEOF)).

tokenizer( IDDoc, [ Parola | Lista ], IDTokenPrecedente) :-
    nextIDToken(IDTok),
    assertFact(kb:token(IDTok, Parola)),
    assertFact(kb:appartiene(IDTok, IDDoc)),
    assertFact(kb:next(IDTokenPrecedente, IDTok)),
    tokenizer( IDDoc, Lista, IDTok).


%% Crea la Knowledge Base
expandKB :-
    write('1 - Parola'),nl,flush_output,
    base:tag_parola,
    write('2 - Numero'),nl,flush_output,
    base:tag_numero,
    write('3 - NewLine'),nl,flush_output,
    kb:tag_newline,
    write('3 - Separatore'),nl,flush_output,
    base:tag_separatore_data,
    write('4 - Comune'),nl,flush_output,
    comune:tag_comune,
    write('5 - CF'),nl,flush_output,
    cf:tag_cf,
    write('6 - Mail'),nl,flush_output,
    mail:tag_mail,
    write('7 - Tel'),nl,flush_output,
    tel:tag_tel,
    write('8 - Persona'),nl,flush_output,
    persona:tag_persona,
    write('9 - Data'),nl,flush_output,
    data:tag_data,
    write('10 - Soggetto'),nl,flush_output,
    persona:tag_soggetto,
    write('11 - Curatore'),nl,flush_output,
    persona:tag_curatore,
    write('12 - Giudice'),nl,flush_output,
    persona:tag_giudice,
    write('13 - richiesta valuta'),nl,flush_output,
    valuta:tag_richiesta_valuta,
    write('14 - Numero Pratica'),nl,flush_output,
    numero_pratica:tag_numero_pratica,
    write('FINE'),nl,flush_output.

%% Mostra i risultati
resultKB :-
    nl,write('$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$'),nl,
	nl,write('                           Tag Estratti                          '),nl,
	nl,write('&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&'),nl,nl,
    comune:riscomune,nl,
    cf:riscf,nl,
    mail:rismail,nl,
    tel:ristel,nl,
    persona:rispersona,nl,
    data:risdata,nl,
    persona:riscuratore,nl,
    persona:rissoggetto,nl,
    persona:risgiudice,nl,
    valuta:risrichiesta_valuta,nl,
    numero_pratica:risnumero_pratica,nl,
    nl,write('$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$'),nl,nl.	

%% Mostra le spiegazioni
explainKB:-
    findall((Tag,Spiegazione), (kb:tag(IDTag, Tag),spiega(IDTag,Spiegazione)), ListaSpiegazioni),
    write('SPIEGAZIONI: '), 
    write(ListaSpiegazioni),nl.

assertFact(Fact):-
    \+( Fact ),
    !,
    assertz(Fact).
assertFact(_).

assertTag(Tag, IDDoc, Spiegazione, Dipendenze) :-
    nextIDTag(IDTag),
    assertFact(kb:tag(IDTag, Tag)),
    assertFact(spiega(IDTag,Spiegazione)),
    assertFact(kb:appartiene(IDTag, IDDoc)),
    forall( member(D,Dipendenze), (assertFact(depends(IDTag, D))) ).

assertTag(Tag, IDDoc, ListaPrecedenti, ListaSuccessivi, Spiegazione) :-
    assertTag(Tag, IDDoc, ListaPrecedenti, ListaSuccessivi, Spiegazione, []).

assertTag(Tag, IDDoc, ListaPrecedenti, ListaSuccessivi, Spiegazione, Dipendenze) :-
    nextIDTag(IDTag),
    assertFact(kb:tag(IDTag, Tag)),
    forall( member( Precedente, ListaPrecedenti ), ( assertFact(kb:next(Precedente, IDTag)) ) ),
    forall( member( Successivo, ListaSuccessivi ), ( assertFact(kb:next(IDTag, Successivo)) ) ),
%    atomic_list_concat( ['Trovato tag:', IDTag, 'con contenuto: '], ' ', Message),
%    write(Message), write(Tag), nl,
    assertFact(spiega(IDTag,Spiegazione)),
    assertFact(kb:appartiene(IDTag, IDDoc)),
    forall( member(D,Dipendenze), (assertFact(depends(IDTag, D))) ).

listaDep([], []) :- !.
listaDep([Tag|AltriTag], Dep) :-
    listaDep(Tag,DepTag),
    listaDep(AltriTag,DepAltri),
    append(DepTag, DepAltri, Dep),
    !.   
listaDep(IDTag, [IDTag|Dep]) :-
    findall(Tag, depends(IDTag, Tag), List),
    listaDep(List,Dep).



spiegaTutto([], '') :- !.
spiegaTutto([Tag|AltriTag], Spiegazioni) :-
    spiega(Tag,SpiegazioneTag),
    spiegaTutto(AltriTag,SpiegazioniAltri),
    atomic_list_concat([SpiegazioneTag, SpiegazioniAltri], '\n', Spiegazioni),
    !.   
spiegaTutto(IDTag, Stringa) :-
    spiega(IDTag, Spiegazione),
    findall(Tag, (depends(IDTag, Tag), kb:tag(Tag,_)), ListaDipendenze),
    spiegaTutto(ListaDipendenze, RestoSpiegazione),
    atomic_list_concat([Spiegazione, RestoSpiegazione], '\n', Stringa).

nextIDTag(IDTag) :- 
    lastIDTag(LastTag),
    NewTag is LastTag+1,
    atom_number(AtomNewTag, NewTag),
    atom_concat('tag', AtomNewTag, IDTag),
    retract(lastIDTag(_)),
    asserta(lastIDTag(NewTag)),
    !.
nextIDTag(IDTag) :-
    asserta(lastIDTag(0)),
    IDTag = 'tag0',
    !.

nextIDToken(IDToken) :- 
    lastIDToken(LastToken),
    NewToken is LastToken+1,
    atom_number(AtomNewToken, NewToken),
    atom_concat('t', AtomNewToken, IDToken),
    retract(lastIDToken(_)),
    asserta(lastIDToken(NewToken)),
    !.
nextIDToken(IDToken) :-
    asserta(lastIDToken(0)),
    IDToken = 't0',
    !.

nextIDDocument(IDDoc) :- 
    lastIDDocument(LastDoc),
    NewDoc is LastDoc+1,
    atom_number(AtomNewDoc, NewDoc),
    atom_concat('doc', AtomNewDoc, IDDoc),
    retract(lastIDDocument(_)),
    asserta(lastIDDocument(NewDoc)),
    !.
nextIDDocument(IDDoc) :-
    asserta(lastIDDocument(0)),
    IDDoc = 'doc0',
    !.

newline(ID) :- 
    (kb:token(ID, '\n'));
    (kb:tag(ID, newline(_))).

tag_newline :-
    findall(_, tag_newline(_), _).

tag_newline(IDToken) :-
    kb:token(IDToken, '\n'),
    findall( Precedente, kb:next(Precedente, IDToken), ListaPrecedenti ),
    findall( Successivo, kb:next(IDToken, Successivo), ListaSuccessivi ),
    atomic_list_concat(['[NEW LINE] Presenza nel documento del newline'],' ',Spiegazione),
    kb:appartiene(IDToken, IDDoc),
    assertTag(newline(IDToken), IDDoc, ListaPrecedenti, ListaSuccessivi, Spiegazione, [IDToken]).


vicini(ID1, ID2) :- 
    kb:next(ID1, ID2), !.
vicini(ID1, ID2) :- 
    kb:next(ID2, ID1), !.

stessa_frase(ID1, ID1).
stessa_frase(ID1, ID2) :-
    \+newline(ID1),
    \+newline(ID2),
    kb:vicini(ID1, ID2),
    !.
stessa_frase(ID1, ID2) :-
    appartiene(ID1, Doc1),
    appartiene(ID2, Doc2),
    Doc1\=Doc2,
    !,
    fail.
stessa_frase(ID1, ID2) :-
    seguente_in_frase(ID1,ID2).
stessa_frase(ID1, ID2) :-
    seguente_in_frase(ID2,ID1).

seguente_in_frase(ID1, ID2) :-
    \+newline(ID1),
    \+newline(ID2),
    kb:next(ID1, ID2),
    !.
seguente_in_frase(ID1, ID2) :-
    \+newline(ID1),
    \+newline(ID2),
    kb:next(ID1, ID3),
    \+newline(ID3),
    !,
    seguente_in_frase(ID3,ID2).
