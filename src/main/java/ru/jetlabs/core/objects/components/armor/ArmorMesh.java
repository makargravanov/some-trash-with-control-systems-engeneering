package ru.jetlabs.core.objects.components.armor;

import java.util.Random;

public class ArmorMesh {
    public final Armor[] grid;
    public int size; // длина слоя
    public int depth; // количество слоёв

    public ArmorMesh(int size, int depth, ArmorMaterial material) {
        grid = new Armor[size * depth];
        this.size = size;
        this.depth = depth;
        for (int i = 0; i < grid.length; i++) {
            grid[i] = new Armor(material);
        }
    }

    public ArmorMesh(int size, ArmorMaterial... materials) {
        grid = new Armor[size * materials.length];
        this.size = size;
        this.depth = materials.length;
        for (int j = 0; j < depth; j++) {
            for (int i = 0; i < size; i++) {
                setElem(new Armor(materials[j]), i, j);
            }
        }
    }

    public double mass() {
        double total = 0;
        for (Armor a : grid) {
            total += a.mass;
        }
        return total;
    }

    public DamageResult applyKineticDamage(double mass, double speed) {
        double energy = 0.5 * mass * (speed * speed);
        int hitPoint = new Random().nextInt(size);
        double residualEnergy = energy;

        for (int layer = 0; layer < depth; layer++) {
            Armor currentElem = getElem(hitPoint, layer);
            if (currentElem == null) {
                continue;
            }

            ArmorMaterial material = currentElem.material;
            double fullDamage = residualEnergy * material.fragility;
            double cellDamage = fullDamage * (1 - material.transferEnergy);
            double transferDamage = fullDamage * material.transferEnergy;

            // Распределяем урон по соседним клеткам
            kineticDamageLeft(hitPoint, layer, transferDamage / 2);
            kineticDamageRight(hitPoint, layer, transferDamage / 2);

            // Обработка разрушения клетки
            if (cellDamage >= currentElem.hp) {
                // Случай: урон превышает прочность клетки
                if (material.fragility >= 1.5) {
                    residualEnergy *= (0.5 / material.fragility);
                } else if (material.fragility >= 1) {
                    if (new Random().nextFloat() < 0.7) {
                        residualEnergy -= (transferDamage + currentElem.hp) / material.fragility;
                    } else {
                        residualEnergy -= (transferDamage + currentElem.hp / 2) / material.fragility;
                    }
                } else {
                    residualEnergy *= 2.0 / 3;
                }
                setElem(null, hitPoint, layer); // Уничтожаем клетку
            } else {
                // Случай: урон меньше прочности клетки
                currentElem.hp -= cellDamage;
                boolean broken = false;

                if (material.fragility >= 1.5) {
                    double initialHp = currentElem.hp + cellDamage;
                    double breakChance = cellDamage / initialHp;
                    if (new Random().nextFloat() < breakChance) {
                        broken = true;
                        residualEnergy *= (0.5 / material.fragility);
                    }
                }

                if (broken) {
                    setElem(null, hitPoint, layer); // Уничтожаем клетку
                } else {
                    residualEnergy = 0;
                    break;
                }
            }

            if (residualEnergy <= 0) {
                residualEnergy = 0;
                break;
            }
        }

        return new DamageResult(residualEnergy);
    }

    private void kineticDamageRight(int currentNumber, int currentLine, double transferDamage) {
        Armor right = getRightBy(currentNumber, currentLine).elem();
        if (right != null) {
            right.hp -= (transferDamage - transferDamage * right.material.transferEnergy);
            transferDamage = transferDamage * right.material.transferEnergy;
            if (right.hp <= 0) {
                setRightBy(null, currentNumber, currentLine);
            }
        } else {
            return;
        }
        if (transferDamage <= 30_000) {
            return;
        }
        kineticDamageRight(currentNumber + 1, currentLine, transferDamage / 2);
    }

    private void kineticDamageLeft(int currentNumber, int currentLine, double transferDamage) {
        Armor left = getLeftBy(currentNumber, currentLine).elem();
        if (left != null) {
            left.hp -= (transferDamage - transferDamage * left.material.transferEnergy);
            transferDamage = transferDamage * left.material.transferEnergy;
            if (left.hp <= 0) {
                setLeftBy(null, currentNumber, currentLine);
            }
        } else {
            return;
        }
        if (transferDamage <= 30_000) {
            return;
        }
        kineticDamageLeft(currentNumber - 1, currentLine, transferDamage / 2);
    }


    private Elem getLeftBy(int number, int onLevel) {
        if ((number == 0)) {
            return new Elem(null, ElemResult.SIDE_NULL);
        }
        return new Elem(getElem(number - 1, onLevel), ElemResult.DEFAULT);
    }

    private Elem getRightBy(int number, int onLevel) {
        if ((number == size - 1)) {
            return new Elem(null, ElemResult.SIDE_NULL);
        }
        return new Elem(getElem(number + 1, onLevel), ElemResult.DEFAULT);
    }

    private Elem getUnderBy(int number, int onLevel) {
        if (onLevel == depth - 1) {
            return new Elem(null, ElemResult.BOTTOM_NULL);
        }
        return new Elem(getElem(number - 1, onLevel), ElemResult.DEFAULT);
    }

    private void setLeftBy(Armor armor, int number, int onLevel) {
        setElem(armor, number - 1, onLevel);
    }

    private void setRightBy(Armor armor, int number, int onLevel) {
        setElem(armor, number + 1, onLevel);
    }

    private void setUnderBy(Armor armor, int number, int onLevel) {
        setElem(armor, number - 1, onLevel);
    }

    private record Elem(
            Armor elem,
            ElemResult result
    ) {
    }

    private enum ElemResult {
        DEFAULT,
        SIDE_NULL,
        BOTTOM_NULL
    }

    public Armor getElem(int number, int onLevel) {
        return grid[size * onLevel + number];
    }

    public void setElem(Armor elem, int number, int onLevel) {
        grid[size * onLevel + number] = elem;
    }
}
