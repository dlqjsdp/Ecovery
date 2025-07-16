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
        itemFormDto.setCategory(itemVO.getCategory());
        itemFormDto.setItemDetail(itemVO.getItemDetail());
        itemFormDto.setItemSellStatus(itemVO.getItemSellStatus());
        itemFormDto.setCategory(itemVO.getCategory());

        return itemFormDto;
    }

    //ItemFormDto -> ItemVO 변환 메소드
    private ItemVO convertDtoToVo(ItemFormDto itemFormDto) {
        return ItemVO.builder()
                .itemId(itemFormDto.getItemId())
                .itemName(itemFormDto.getItemNm())
                .stockNumber(itemFormDto.getStockNumber())
                .category(itemFormDto.getCategory())
                .itemDetail(itemFormDto.getItemDetail())
                .itemSellStatus(itemFormDto.getItemSellStatus())
                .build();
    }

    //상품 목록 페이지 (전체 상품 조회)
    @Override
    public List<ItemListDto> getItemList(String itemNm, String category, Criteria cri) {
        log.info("getItemList >>> criteria : {} ", cri);

        //JOIN 쿼리로 Item + 대표 이미지 한 번에 가져옴
        List<ItemListDto> itemList = itemMapper.getListWithPage(itemNm, category, cri);

        return itemList;
    }

    //상품 수정 페이지 (상품 단건 조회)
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

        //상품 이미지 저장
        for(MultipartFile multipartFile : itemImgFileList) {
            ItemImgVO itemImg = ItemImgVO.builder()
                    .itemId(itemFormDto.getItemId())
                    .build();

            if(itemImgFileList.get(0).equals(multipartFile)){
                itemImg.setRepImgYn("Y");
            }else {
                itemImg.setRepImgYn("N");
            }

            itemImgService.saveItemImg(itemImg, multipartFile);
        }

        return item.getItemId();
    }

}
