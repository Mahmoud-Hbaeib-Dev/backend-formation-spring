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
    
    // D'abord, chercher si il y a un deuxième JSON (objet d'erreur) qui commence par {"timestamp" ou {"error"
    const errorJsonStart = data.indexOf('{"timestamp"', startIndex);
    const errorJsonStart2 = data.indexOf('{"error"', startIndex);
    const secondJsonStart = errorJsonStart !== -1 ? errorJsonStart : (errorJsonStart2 !== -1 ? errorJsonStart2 : -1);
    
    // Si on a trouvé un deuxième JSON, essayer de parser seulement jusqu'à ce point
    if (secondJsonStart > startIndex) {
      // Chercher la fin du premier JSON juste avant le début du deuxième
      // En partant de secondJsonStart et en reculant caractère par caractère jusqu'à trouver un JSON valide
      // On recule progressivement depuis le début du deuxième JSON
      for (let tryEnd = secondJsonStart - 1; tryEnd > startIndex + 10; tryEnd--) {
        const candidate = data.substring(startIndex, tryEnd + 1).trim();
        // Vérifier que ça se termine par le bon caractère
        if (candidate.endsWith(endChar)) {
          try {
            const parsed = JSON.parse(candidate);
            console.log(`✅ [JSON PARSER] JSON valide extrait (détection du deuxième JSON à la position ${secondJsonStart}, parsé jusqu'à ${tryEnd})`);
            return parsed;
          } catch (parseError) {
            // Continuer à essayer avec une position plus petite
          }
        }
      }
    }

    // Algorithme amélioré qui compte les accolades/crochets avec limite de sécurité
    let depth = 0;
    let endIndex = -1;
    let inString = false;
    let escapeNext = false;
    const maxLength = Math.min(data.length, startIndex + 2000000); // Limite de sécurité augmentée

    for (let i = startIndex; i < maxLength; i++) {
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
      
      // Essayer de trouver un JSON valide en cherchant depuis la fin
      // Commencer par chercher où commence le deuxième JSON (si présent)
      const secondJsonStart = data.indexOf('{"timestamp"', endIndex === -1 ? startIndex + 100 : endIndex);
      if (secondJsonStart > startIndex) {
        // Il y a probablement un deuxième JSON, essayer de parser jusqu'à ce point
        for (let tryEnd = secondJsonStart - 1; tryEnd > startIndex + 10; tryEnd--) {
          try {
            const candidate = data.substring(startIndex, tryEnd + 1);
            // Vérifier que ça se termine par le bon caractère
            if (candidate.trim().endsWith(endChar)) {
              const parsed = JSON.parse(candidate.trim());
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
      for (let tryEnd = maxTry; tryEnd > startIndex + 10; tryEnd -= 100) {
        try {
          const candidate = data.substring(startIndex, tryEnd + 1);
          if (candidate.trim().endsWith(endChar)) {
            const parsed = JSON.parse(candidate.trim());
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

    // Extraire et parser la partie JSON valide
    const jsonPart = data.substring(startIndex, endIndex + 1);
    
    try {
      return JSON.parse(jsonPart);
    } catch (parseError) {
      // Si le parsing échoue, essayer une approche alternative
      console.warn('⚠️ [JSON PARSER] Erreur lors du parsing, tentative alternative...');
      
      // Chercher le premier JSON valide en essayant depuis différentes positions
      for (let tryEnd = endIndex; tryEnd > startIndex + 10; tryEnd--) {
        try {
          const candidate = data.substring(startIndex, tryEnd + 1);
          if (candidate.endsWith(endChar)) {
            const parsed = JSON.parse(candidate);
            console.log(`✅ [JSON PARSER] JSON valide trouvé avec approche alternative`);
            return parsed;
          }
        } catch (e) {
          // Continuer à essayer
        }
      }
      
      console.error('❌ [JSON PARSER] Erreur lors du parsing:', parseError);
      return null;
    }
  }
}

