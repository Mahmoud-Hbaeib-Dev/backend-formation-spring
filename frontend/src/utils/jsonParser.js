/**
 * Utilitaire pour parser les réponses JSON qui peuvent contenir du texte supplémentaire
 * (par exemple, des erreurs sérialisées après le JSON valide)
 */
export function parseJsonSafely(jsonString) {
  if (typeof jsonString !== 'string') {
    return jsonString;
  }

  try {
    // Essayer de parser directement
    return JSON.parse(jsonString);
  } catch (e) {
    // Si ça échoue, essayer d'extraire seulement la partie JSON valide
    try {
      // Chercher le premier caractère de début de JSON ([ ou {)
      const startIndex = jsonString.search(/[\[{]/);
      if (startIndex === -1) {
        console.warn('Aucun JSON trouvé dans la chaîne');
        return null;
      }

      const startChar = jsonString[startIndex];
      const endChar = startChar === '[' ? ']' : '}';

      // Algorithme amélioré qui ignore les caractères à l'intérieur des chaînes JSON
      let depth = 0;
      let endIndex = -1;
      let inString = false;
      let escapeNext = false;

      for (let i = startIndex; i < jsonString.length; i++) {
        const char = jsonString[i];

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
        console.warn('JSON incomplet dans la chaîne');
        return null;
      }

      // Extraire et parser la partie JSON valide
      const jsonPart = jsonString.substring(startIndex, endIndex + 1);
      
      // Essayer de parser
      try {
        return JSON.parse(jsonPart);
      } catch (parseError) {
        // Si ça échoue encore, essayer de trouver un JSON plus court
        // en cherchant récursivement depuis la fin
        for (let tryEnd = endIndex - 1; tryEnd > startIndex; tryEnd--) {
          try {
            const shorterJson = jsonString.substring(startIndex, tryEnd + 1);
            return JSON.parse(shorterJson);
          } catch (e) {
            // Continuer à essayer
          }
        }
        throw parseError;
      }
    } catch (parseError) {
      // Dernière tentative : chercher le premier JSON valide avec une regex
      try {
        // Chercher un tableau ou un objet JSON complet
        const jsonMatch = jsonString.match(/(\[[\s\S]*?\]|\{[\s\S]*?\})/);
        if (jsonMatch) {
          // Essayer de parser le match le plus long possible
          for (let len = jsonMatch[0].length; len > 10; len--) {
            try {
              const candidate = jsonMatch[0].substring(0, len);
              return JSON.parse(candidate);
            } catch (e) {
              // Continuer
            }
          }
        }
      } catch (e) {
        // Ignorer
      }
      
      console.error('Erreur lors de l\'extraction du JSON:', parseError);
      return null;
    }
  }
}

