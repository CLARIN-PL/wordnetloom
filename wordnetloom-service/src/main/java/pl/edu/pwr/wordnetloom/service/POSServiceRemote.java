package pl.edu.pwr.wordnetloom.service;

import java.util.List;

import javax.ejb.Remote;

import pl.edu.pwr.wordnetloom.model.PartOfSpeech;

@Remote
public interface POSServiceRemote {

	PartOfSpeech getPOSByID(Long id);
	List<PartOfSpeech> getAllPartsOfSpeech(List<Long> usedLexicons);
	List<PartOfSpeech> getAllPartsOfSpeech();
}
