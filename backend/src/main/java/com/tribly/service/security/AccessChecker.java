package com.tribly.service.security;

import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import java.util.List;

public interface AccessChecker {
  EntityType getType();

  boolean hasRights(ActionType action, List<Object> params);
  /*
   default @Nullable Object getFirstParam(List<Object> params) {
     if (params.isEmpty()) {
       return null;
     }
     return params.getFirst();
   }

   default @Nullable String getFirstParamAsString(List<Object> params) {
     Object first = getFirstParam(params);
     return paramString(first);
   }

   @Nullable
   default Long getFirstParamAsLong(List<Object> params) {
     Object first = getFirstParam(params);
     return paramLong(first);
   }

   default @Nullable Long paramLong(@Nullable Object first) {
     return switch (first) {
       case Long l -> l;
       case String l -> TsidUtils.toLong(l);
       case null, default -> null;
     };
   }

   default @Nullable String paramString(@Nullable Object first) {
     return switch (first) {
       case String l -> l;
       case null, default -> null;
     };
   }

  */
}
