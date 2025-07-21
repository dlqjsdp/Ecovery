package com.ecovery.service;

import com.ecovery.domain.ItemImgVO;
import com.ecovery.domain.ItemVO;
import com.ecovery.dto.Criteria;
import com.ecovery.dto.ItemFormDto;
import com.ecovery.dto.ItemImgDto;
import com.ecovery.dto.ItemListDto;
import com.ecovery.mapper.ItemImgMapper;
import com.ecovery.mapper.ItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 에코마켓 상품 ServiceImpl
 * @author : sehui
 * @fileName : ItemServiceImpl
 * @since : 250709
 * @history
 *  - 250710 | sehui | 상품 단건 조회 기능 추가
 *  - 250714 | sehui | 전체 상품 조회 기능 추가
 *  - 250715 | sehui | 전체 상품의 수 조회 기능 추가
 *  - 250716 | sehui | VO -> DTO, DTO -> VO 변환 메소드 추가
 *  - 250716 | sehui | 상품 등록 기능 추가
 *  - 250717 | sehui | 상품 수정 기능 추가
 *  - 250718 | sehui | 상품 삭제 기능 추가
 */

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService     {

    private final ItemMapper itemMapper;
    private final ItemImgMapper itemImgMapper;
    private final ItemImgService itemImgService;

    //ItemImgVo -> ItemImgDto 변환 메소드
    private List<ItemImgDto> convertImgVoToDto(List<ItemImgVO> itemImgList) {
        return itemImgList.stream()
                .map(vo -> new ItemImgDto(vo.getItemImgId(), vo.getItemId(), vo.getImgName(), vo.getOriImgName(), vo.getImgUrl(), vo.getRepImgYn()))
                .collect(Collectors.toList());
    }

    //ItemVO -> ItemFormDto 변환 메소드
    private ItemFormDto convertVoToDto(ItemVO itemVO) {
        ItemFormDto itemFormDto = new ItemFormDto();

        itemFormDto.setItemId(itemVO.getItemId());
        itemFormDto.setItemNm(itemVO.getItemName());
        itemFormDto.setPrice(itemVO.getPrice());
        itemFormDto.setStockNumber(itemVO.getStockNumber());
        itemFormDto.setCategoryId(itemVO.getCategoryId());
        itemFormDto.setItemDetail(itemVO.getItemDetail());
        itemFormDto.setItemSellStatus(itemVO.getItemSellStatus());

        return itemFormDto;
    }

    //ItemFormDto -> ItemVO 변환 메소드
    private ItemVO convertDtoToVo(ItemFormDto itemFormDto) {
        return ItemVO.builder()
                .itemId(itemFormDto.getItemId())
                .itemName(itemFormDto.getItemNm())
                .price(itemFormDto.getPrice())
                .stockNumber(itemFormDto.getStockNumber())
                .categoryId(itemFormDto.getCategoryId())
                .itemDetail(itemFormDto.getItemDetail())
                .itemSellStatus(itemFormDto.getItemSellStatus())
                .build();
    }

    //전체 상품 조회
    @Override
    public List<ItemListDto> getItemList(String itemNm, String category, Criteria cri) {

        //JOIN 쿼리로 Item + 대표 이미지 한 번에 가져옴
        List<ItemListDto> itemList = itemMapper.getListWithPage(itemNm, category, cri);

        return itemList;
    }

    //상품 단건 조회
    @Override
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        log.info("getItem >>>");

        //ItemImgVO 조회
        List<ItemImgVO> itemImgList = itemImgMapper.getItemList(itemId);

        //ItemImgVo -> ItemImgDto로 변환
        List<ItemImgDto> itemImgDtoList = convertImgVoToDto(itemImgList);

        //ItemVO 조회
        ItemVO item = itemMapper.getItemDtl(itemId);

        //ItemVO -> ItemFormDto로 변환
        ItemFormDto itemFormDto = convertVoToDto(item);

        //itemImgId만 추출
        List<Long> itemImgIdList = itemImgList.stream()
                .map(ItemImgVO::getItemImgId)
                .collect(Collectors.toList());

        itemFormDto.setItemImgDtoList(itemImgDtoList);
        itemFormDto.setItemImgId(itemImgIdList);

        return itemFormDto;
    }

    //전체 상품의 수 조회
    @Override
    public int getTotalCount(String itemNm, String category) {

        int totalCount = itemMapper.getTotalCount(itemNm, category);

        return totalCount;
    }

    //상품 등록
    @Override
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //ItemFormDto -> ItemVO 변환
        ItemVO item = convertDtoToVo(itemFormDto);

        //상품 정보저장
        itemMapper.insertItem(item);

        if(item.getItemId() == null) {
            throw new RuntimeException("상품 ID가 null입니다.");
        }

        //상품 이미지 저장
        for(int i=0; i<itemImgFileList.size(); i++) {
            MultipartFile multipartFile = itemImgFileList.get(i);

            ItemImgVO itemImg = ItemImgVO.builder()
                    .itemId(item.getItemId())
                    .build();

            //대표 이미지 설정
            if(i == 0){
                itemImg.setRepImgYn("Y");
            }else {
                itemImg.setRepImgYn("N");
            }

            itemImgService.saveItemImg(itemImg, multipartFile);
        }

        return item.getItemId();
    }

    //상품 수정
    @Override
    public boolean updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //ItemFormDto -> ItemVO 변환
        ItemVO item = convertDtoToVo(itemFormDto);

        //상품 정보 수정
        int updatedItemCount = itemMapper.updateItem(item);

        int updatedImgCount = 0;

        //상품 이미지 수정
        List<Long> itemImgIds = itemFormDto.getItemImgId();

        for(int i=0; i<itemImgFileList.size(); i++) {
            MultipartFile multipartFile = itemImgFileList.get(i);
            Long itemImgId = itemImgIds.get(i);

            //ItemImgVo 객체 생성
            ItemImgVO itemImg = ItemImgVO.builder()
                    .itemId(itemFormDto.getItemId())
                    .itemImgId(itemImgId)
                    .build();

            //대표 이미지 설정
            if(i == 0){
                itemImg.setRepImgYn("Y");
            }else {
                itemImg.setRepImgYn("N");
            }

            boolean isUpdated = itemImgService.updateItemImg(itemImg, multipartFile);

            //수정된 이미지 개수
            if(isUpdated) {
                updatedImgCount++;
            }
        }

        return (updatedItemCount == 1) & (updatedImgCount == itemImgFileList.size());
    }

    //상품 삭제
    @Override
    public boolean deleteItem(Long itemId) {

        //상품 이미지 삭제
        int deletedImgCount = itemImgMapper.deleteItemImg(itemId);

        //상품 삭제
        int deletedItemCount = itemMapper.deleteItem(itemId);
        
        //이미지 최소 1개 이상 삭제, 상품 1개 삭제되면 성공
        return deletedImgCount >= 1 && deletedItemCount == 1;
    }
}
