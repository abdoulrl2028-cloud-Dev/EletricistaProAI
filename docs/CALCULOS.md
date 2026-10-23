# Cálculos da versão 0.1

Estes modelos são didáticos. Eles não dimensionam condutor, disjuntor, DR ou DPS e não aplicam tabelas de capacidade de corrente. A instalação real continua dependendo da norma aplicável, das condições de instalação e de um profissional habilitado. O texto da ABNT NBR 5410 não é reproduzido aqui.

## Lei de Ohm

Para resistência constante em corrente contínua:

`U = R × I`

| Entrada | Resultado de referência |
|---|---|
| 12 V e 0,5 A | 24 Ω |
| 10 Ω e 2 A | 20 V |
| 12 V e 4 Ω | 3 A |
| 10 V, 2 A e 5 Ω | conferido |
| 10 V, 2 A e 10 Ω | rejeitado, porque não fecha a equação |

Resistência zero com tensão informada é rejeitada. Corrente zero não produz resistência finita neste modelo.

## Potência em corrente contínua

`P = U × I`

220 V e 5 A produzem 1100 W.

## Corrente estimada

Monofásico: `I = P / (U × FP)`

1100 W, 220 V e fator de potência 1 produzem 5 A.

Trifásico, usando tensão de linha: `I = P / (√3 × U × FP)`

3000 W, 380 V e fator de potência 0,9 produzem cerca de 5,064 A.

O fator de potência fica restrito ao intervalo (0, 1]. O resultado não escolhe cabo nem disjuntor.

## Consumo

`kWh = P(W) × horas / 1000`

`custo = kWh × tarifa`

1000 W durante 2 h são 2 kWh. A 0,90 por kWh, o custo é 1,80 na moeda da tarifa informada. Não há conversão de câmbio.
