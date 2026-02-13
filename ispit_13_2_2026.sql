CREATE DATABASE JavaAdv

USE JavaAdv

CREATE TABLE Polaznik
(
	PolaznikID int CONSTRAINT PK_Polaznik PRIMARY KEY IDENTITY,
	Ime nvarchar(100) NOT NULL,
	Prezime nvarchar(100) NOT NULL
)

CREATE TABLE ProgramObrazovanja
(
	ProgramObrazovanjaID int CONSTRAINT PK_ProgramObrazovanja PRIMARY KEY IDENTITY,
	Naziv nvarchar(100) NOT NULL,
	CSVET int NOT NULL
)

CREATE TABLE Upis
(
	UpisID int CONSTRAINT PK_Upis PRIMARY KEY IDENTITY,
	IDProgramObrazovanja int CONSTRAINT FK_Upis_ProgramObrazovanja FOREIGN KEY REFERENCES ProgramObrazovanja(ProgramObrazovanjaID) NOT NULL,
	IDPolaznik int CONSTRAINT FK_Upis_Polaznik FOREIGN KEY REFERENCES Polaznik(PolaznikID) NOT NULL
)

GO
CREATE PROC UnesiPolaznika @ime NVARCHAR(100), @prezime NVARCHAR(100)
AS
BEGIN
	INSERT INTO Polaznik(Ime, Prezime) VALUES(@ime, @prezime)
END

GO
CREATE PROC UnesiProgramObrazovanja @naziv NVARCHAR(100), @csvet INT
AS
BEGIN
	INSERT INTO ProgramObrazovanja(Naziv, CSVET) VALUES(@naziv, @csvet)
END

GO
CREATE PROC UpisiPolaznika @IDpolaznik INT, @IDprogramObrazovanja INT
AS
BEGIN
	INSERT INTO Upis(IDPolaznik, IDProgramObrazovanja) VALUES(@IDpolaznik, @IDprogramObrazovanja)
END

GO
CREATE PROC PrebaciPolaznika @IDpolaznik INT, @IDstariProgram INT, @IDnoviProgram INT
AS
BEGIN
	UPDATE Upis
	SET IDProgramObrazovanja = @IDnoviProgram
	WHERE IDPolaznik = @IDpolaznik AND IDProgramObrazovanja = @IDstariProgram
END

GO
CREATE PROC PrintProgramObrazovanja @IDprogram INT
AS
BEGIN
	SELECT po.Naziv, po.CSVET, p.Ime, p.Prezime FROM ProgramObrazovanja AS po
	INNER JOIN Upis AS u ON po.ProgramObrazovanjaID = u.IDProgramObrazovanja
	INNER JOIN Polaznik AS p ON p.PolaznikID = u.IDPolaznik
	WHERE po.ProgramObrazovanjaID = @IDprogram
END