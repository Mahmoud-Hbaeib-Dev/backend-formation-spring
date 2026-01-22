/**
 * Utilitaire pour parser les réponses JSON qui peuvent contenir du texte supplémentaire
 * (par exemple, des erreurs sérialisées après le JSON valide)
 */
export function parseJsonSafely(data) {
  // Si ce n'est pas une chaîne, retourner tel quel (déjà parsé)
  if (typeof data !== 'string') {
    return data;
  }

  // Si la chaîne est vide, retourner null
  if (!data || data.trim() === '') {
    return null;
  }

  try {
    // Essayer de parser directement
    return JSON.parse(data);
  } catch (e) {
    // Si ça échoue, essayer d'extraire seulement la partie JSON valide
    // Chercher le premier caractère de début de JSON ([ ou {)
    const startIndex = data.search(/[\[{]/);
    if (startIndex === -1) {
      console.warn('⚠️ [JSON PARSER] Aucun JSON trouvé dans la chaîne');
      return null;
    }

    const startChar = data[startIndex];
    const endChar = startChar === '[' ? ']' : '}';
    
    // Chercher tous les patterns qui indiquent le début d'un objet d'erreur
    const errorPatterns = [
      '{"timestamp"',
      '{"error"',
      '{"status"',
      '{"message"',
      '{"path"',
      '{"type"'
    ];
    
    // Trouver la position la plus proche du début d'un objet d'erreur
    let secondJsonStart = -1;
    for (const pattern of errorPatterns) {
      const pos = data.indexOf(pattern, startIndex);
      if (pos !== -1 && (secondJsonStart === -1 || pos < secondJsonStart)) {
        secondJsonStart = pos;
      }
    }
    
    // Si on a trouvé un deuxième JSON, essayer de parser seulement jusqu'à ce point
    if (secondJsonStart > startIndex) {
      // Chercher la fin du premier JSON juste avant le début du deuxième
      // On recule progressivement depuis le début du deuxième JSON
      // Utiliser une approche binaire pour être plus efficace
      let left = startIndex + 10;
      let right = secondJsonStart - 1;
      let bestResult = null;
      let bestEnd = -1;
      
      // Essayer plusieurs positions autour de la position estimée
      const positionsToTry = [];
      for (let i = right; i >= left; i -= Math.max(1, Math.floor((right - left) / 100))) {
        positionsToTry.push(i);
      }
      // Ajouter aussi quelques positions proches de la fin estimée
      for (let i = right; i >= Math.max(left, right - 1000); i--) {
        positionsToTry.push(i);
      }
      
      for (const tryEnd of positionsToTry) {
        if (tryEnd <= startIndex) continue;
        const candidate = data.substring(startIndex, tryEnd + 1).trim();
        // Vérifier que ça se termine par le bon caractère
        if (candidate.endsWith(endChar)) {
          try {
            const parsed = JSON.parse(candidate);
            // Si on arrive ici, c'est un JSON valide
            bestResult = parsed;
            bestEnd = tryEnd;
            // On continue à chercher une position plus proche du deuxième JSON
            // pour avoir le JSON le plus complet possible
          } catch (parseError) {
            // Continuer à essayer
          }
        }
      }
      
      if (bestResult !== null) {
        console.log(`✅ [JSON PARSER] JSON valide extrait (détection du deuxième JSON à la position ${secondJsonStart}, parsé jusqu'à ${bestEnd})`);
        return bestResult;
      }
    }

    // Algorithme amélioré qui compte les accolades/crochets avec limite de sécurité
    // S'arrête aussi si on détecte le début d'un objet d'erreur
    let depth = 0;
    let endIndex = -1;
    let inString = false;
    let escapeNext = false;
    const maxLength = Math.min(data.length, startIndex + 2000000); // Limite de sécurité augmentée
    
    // Trouver la position la plus proche d'un pattern d'erreur pour limiter la recherche
    let earliestErrorPos = maxLength;
    for (const pattern of errorPatterns) {
      const pos = data.indexOf(pattern, startIndex);
      if (pos !== -1 && pos < earliestErrorPos) {
        earliestErrorPos = pos;
      }
    }
    const searchLimit = Math.min(maxLength, earliestErrorPos);

    for (let i = startIndex; i < searchLimit; i++) {
      const char = data[i];

      if (escapeNext) {
        escapeNext = false;
        continue;
      }

      if (char === '\\') {
        escapeNext = true;
        continue;
      }

      if (char === '"' && !escapeNext) {
        inString = !inString;
        continue;
      }

      if (!inString) {
        if (char === startChar) {
          depth++;
        } else if (char === endChar) {
          depth--;
          if (depth === 0) {
            endIndex = i;
            break;
          }
        }
      }
    }

    if (endIndex === -1) {
      // Si on n'a pas trouvé la fin, essayer une approche alternative :
      // Chercher le premier JSON valide en essayant de parser depuis différentes positions
      console.warn('⚠️ [JSON PARSER] JSON incomplet, tentative de parsing progressif...');
      
      // Chercher à nouveau les patterns d'erreur (réutiliser la variable du scope parent)
      if (secondJsonStart === -1) {
        for (const pattern of errorPatterns) {
          const pos = data.indexOf(pattern, startIndex);
          if (pos !== -1 && (secondJsonStart === -1 || pos < secondJsonStart)) {
            secondJsonStart = pos;
          }
        }
      }
      
      if (secondJsonStart > startIndex) {
        // Il y a probablement un deuxième JSON, essayer de parser jusqu'à ce point
        // Utiliser une recherche binaire améliorée
        const searchRange = Math.min(secondJsonStart - startIndex, 10000);
        for (let offset = 0; offset < searchRange; offset += Math.max(1, Math.floor(searchRange / 200))) {
          const tryEnd = secondJsonStart - 1 - offset;
          if (tryEnd <= startIndex) break;
          try {
            const candidate = data.substring(startIndex, tryEnd + 1).trim();
            if (candidate.endsWith(endChar)) {
              const parsed = JSON.parse(candidate);
              console.log(`✅ [JSON PARSER] JSON valide trouvé jusqu'à la position ${tryEnd}`);
              return parsed;
            }
          } catch (e) {
            // Continuer à essayer
          }
        }
      }
      
      // Si on n'a pas trouvé, essayer depuis la fin de manière plus agressive
      const maxTry = Math.min(data.length - 1, startIndex + 500000);
      const step = Math.max(1, Math.floor((maxTry - startIndex) / 500));
      for (let tryEnd = maxTry; tryEnd > startIndex + 10; tryEnd -= step) {
        try {
          const candidate = data.substring(startIndex, tryEnd + 1).trim();
          if (candidate.endsWith(endChar)) {
            const parsed = JSON.parse(candidate);
            console.log(`✅ [JSON PARSER] JSON valide trouvé avec parsing progressif à la position ${tryEnd}`);
            return parsed;
          }
        } catch (e) {
          // Continuer à essayer
        }
      }
      
      console.warn('⚠️ [JSON PARSER] Impossible de trouver un JSON valide');
      return null;
    }

    // Réutiliser secondJsonStart si déjà trouvé, sinon le chercher à nouveau
    if (secondJsonStart === -1) {
      for (const pattern of errorPatterns) {
        const pos = data.indexOf(pattern, startIndex);
        if (pos !== -1 && (secondJsonStart === -1 || pos < secondJsonStart)) {
          secondJsonStart = pos;
        }
      }
    }
    
    // Si on a trouvé un pattern d'erreur, l'utiliser en priorité (même s'il est après endIndex)
    // car endIndex peut être incorrect pour de très gros JSON
    if (secondJsonStart > startIndex) {
      // Chercher le JSON valide juste avant le pattern d'erreur
      const searchEnd = secondJsonStart - 1;
      const searchRange = searchEnd - startIndex;
      
      // Utiliser une recherche binaire améliorée
      let bestResult = null;
      let bestEnd = -1;
      
      // Essayer plusieurs positions avec un pas adaptatif
      const step = Math.max(1, Math.floor(searchRange / 300));
      for (let tryEnd = searchEnd; tryEnd > startIndex + 10; tryEnd -= step) {
        try {
          const candidate = data.substring(startIndex, tryEnd + 1).trim();
          if (candidate.endsWith(endChar)) {
            const parsed = JSON.parse(candidate);
            bestResult = parsed;
            bestEnd = tryEnd;
            // Continuer à chercher une position plus proche du pattern d'erreur
            // pour avoir le JSON le plus complet
          }
        } catch (e) {
          // Continuer à essayer silencieusement
        }
      }
      
      // Si on a trouvé un résultat, essayer de l'améliorer en cherchant plus près du pattern
      if (bestResult !== null && bestEnd < searchEnd - 100) {
        for (let tryEnd = Math.min(searchEnd - 1, bestEnd + 1000); tryEnd > bestEnd; tryEnd -= 10) {
          try {
            const candidate = data.substring(startIndex, tryEnd + 1).trim();
            if (candidate.endsWith(endChar)) {
              const parsed = JSON.parse(candidate);
              bestResult = parsed;
              bestEnd = tryEnd;
            }
          } catch (e) {
            // Continuer silencieusement
          }
        }
      }
      
      if (bestResult !== null) {
        // Ne logger que si c'est différent de endIndex (pour éviter les logs inutiles)
        if (bestEnd !== endIndex) {
          console.log(`✅ [JSON PARSER] JSON valide extrait (pattern d'erreur détecté à ${secondJsonStart}, parsé jusqu'à ${bestEnd})`);
        }
        return bestResult;
      }
    }
    
    // Si pas de pattern d'erreur trouvé ou si la recherche précédente a échoué,
    // essayer avec endIndex
    const jsonPart = data.substring(startIndex, endIndex + 1);
    
    try {
      return JSON.parse(jsonPart);
    } catch (parseError) {
      // Si le parsing échoue, essayer une approche alternative (sans logger d'erreur pour l'instant)
      // Chercher le pattern d'erreur si pas encore trouvé
      if (secondJsonStart === -1) {
        for (const pattern of errorPatterns) {
          const pos = data.indexOf(pattern, startIndex);
          if (pos !== -1 && (secondJsonStart === -1 || pos < secondJsonStart)) {
            secondJsonStart = pos;
          }
        }
      }
      
      // Si on a trouvé un deuxième JSON, utiliser cette position
      const searchEnd = secondJsonStart > startIndex 
        ? secondJsonStart - 1 
        : endIndex;
      const searchRange = searchEnd - startIndex;
      const step = Math.max(1, Math.floor(searchRange / 300));
      
      // Chercher le premier JSON valide en essayant depuis différentes positions
      let bestResult = null;
      let bestEnd = -1;
      
      for (let tryEnd = searchEnd; tryEnd > startIndex + 10; tryEnd -= step) {
        try {
          const candidate = data.substring(startIndex, tryEnd + 1).trim();
          if (candidate.endsWith(endChar)) {
            const parsed = JSON.parse(candidate);
            bestResult = parsed;
            bestEnd = tryEnd;
            // Continuer pour trouver le JSON le plus complet possible
          }
        } catch (e) {
          // Continuer à essayer silencieusement
        }
      }
      
      if (bestResult !== null) {
        console.log(`✅ [JSON PARSER] JSON valide trouvé avec approche alternative à la position ${bestEnd}`);
        return bestResult;
      }
      
      // Dernière tentative: essayer de trouver n'importe quel JSON valide
      const maxTry = Math.min(data.length - 1, startIndex + 10000);
      for (let tryEnd = maxTry; tryEnd > startIndex + 10; tryEnd -= 50) {
        try {
          const candidate = data.substring(startIndex, tryEnd + 1).trim();
          if (candidate.endsWith(endChar)) {
            const parsed = JSON.parse(candidate);
            console.log(`✅ [JSON PARSER] JSON valide trouvé en dernier recours à la position ${tryEnd}`);
            return parsed;
          }
        } catch (e) {
          // Continuer à essayer silencieusement
        }
      }
      
      // Seulement logger l'erreur si toutes les tentatives ont échoué
      console.error('❌ [JSON PARSER] Impossible de parser le JSON après toutes les tentatives');
      return null;
    }
  }
}

