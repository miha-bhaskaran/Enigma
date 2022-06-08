package enigma;
import java.util.ArrayList;
import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author athmiha
 */
class Machine {




    /** This is num of Rotors. */
    private int _numRotors;
    /** This is num of Pawls. */
    private int _pawls;
    /** This is plugboard. */
    private Permutation _plugboard;
    /** This is All rotors. */
    private Collection<Rotor> _allRotors;
    /** This is array of Rotors. */
    private ArrayList<Rotor> _rotors = new ArrayList<Rotor>();

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 < PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;



    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors.clear();
        for (String x: rotors) {
            for (Rotor y : _allRotors) {
                if (x.equals(y.name())) {
                    if (!_rotors.contains(y)) {
                        _rotors.add(y);
                    }
                }
            }
        }
        assert _rotors.get(0) instanceof Reflector
                : "Asserting that 0th is a reflector";
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 1; i < _rotors.size(); i++) {
            _rotors.get(i).set(setting.charAt(i - 1));
        }
    }



    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;

    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        ArrayList<Boolean> shudMove = new ArrayList<Boolean>();
        for (int i = 0; i < _rotors.size(); i++) {
            shudMove.add(false);
        }

        if (_rotors.get(numRotors() - 1).atNotch()) {
            shudMove.set(numRotors() - 1, true);
            shudMove.set(numRotors() - 2, true);
        } else {
            shudMove.set(numRotors() - 1, true);
        }
        for (int i = numRotors() - 2; i > 0; i--) {
            if (_rotors.get(i).atNotch() && _rotors.get(i - 1).rotates()) {
                shudMove.set(i, true);
                shudMove.set(i - 1, true);
            }

        }
        for (int i = 0; i < shudMove.size(); i++) {
            if (shudMove.get(i)) {
                _rotors.get(i).advance();
            }
        }


    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int z = c;
        for (int i = _rotors.size() - 1; i > 0; i--) {
            z = _rotors.get(i).convertForward(z);
        }
        z = _rotors.get(0).convertForward(z);

        for (int i = 1; i < _rotors.size(); i++) {
            z = _rotors.get(i).convertBackward(z);
        }
        return z;


    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String word = "";
        for (int i = 0; i < msg.length(); i++) {
            word += _alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i))));
        }
        return word;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

}
