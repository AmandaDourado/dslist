package com.devsuperior.dslist.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dslist.dto.GameListDTO;
import com.devsuperior.dslist.entities.GameList;
import com.devsuperior.dslist.projections.GameMinProjection;
import com.devsuperior.dslist.repositories.GameListRepository;
import com.devsuperior.dslist.repositories.GameRepository;

@Service
public class GameListService {
	
	@Autowired
	private GameListRepository gameListRepository;
	
	@Autowired
	private GameRepository gameRepository;
	
	
	@Transactional(readOnly = true)
	public List<GameListDTO> findAll(){
		List<GameList> result = gameListRepository.findAll();
		return result.stream().map(x -> new GameListDTO(x)).toList();
	}
	
	@Transactional
	public void move(Long listId, int sourceIndex, int destionationIndex) {
		List<GameMinProjection> list = gameRepository.searchByList(listId);
		
		GameMinProjection obj = list.remove(sourceIndex);
		list.add(destionationIndex, obj);
		//a lista de games já está na ordem correta, só precisar alterar o número da posição 
		
		// achar o minimo e o maximo da lista, para não precisar atualizar a lista inteira no banco de dados, alterar apenas o que estava dentro do intervalo
		int min = sourceIndex < destionationIndex ? sourceIndex : destionationIndex;
		int max = sourceIndex < destionationIndex ?  destionationIndex : sourceIndex;
		
		for(int i = min; i <= max; i++) {
			      // o número da lista // pegar o id do jogo // colocar a nova posição
			gameListRepository.updateBelongingPosition(listId, list.get(i).getId(), i);
		}
	}
}
