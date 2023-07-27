package com.hyundaiautoever.HEAT.v1.util;

import com.hyundaiautoever.HEAT.v1.dto.translation.TranslationDto;
import com.hyundaiautoever.HEAT.v1.entity.Translation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-27T14:30:13+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.6.1.jar, environment: Java 11.0.16.1 (Eclipse Adoptium)"
)
public class TranslationMapperImpl implements TranslationMapper {

    @Override
    public TranslationDto toTranslationDto(Translation translation) {
        if ( translation == null ) {
            return null;
        }

        Translation translation1 = null;

        translation1 = translation;

        TranslationDto translationDto = new TranslationDto( translation1 );

        return translationDto;
    }

    @Override
    public List<TranslationDto> toTranslationDtoList(List<Translation> translationList) {
        if ( translationList == null ) {
            return null;
        }

        List<TranslationDto> list = new ArrayList<TranslationDto>( translationList.size() );
        for ( Translation translation : translationList ) {
            list.add( toTranslationDto( translation ) );
        }

        return list;
    }
}
