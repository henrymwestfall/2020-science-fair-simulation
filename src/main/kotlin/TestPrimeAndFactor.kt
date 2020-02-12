object TestPrimeAndFactor {
    @JvmStatic fun main(args: Array<String>) {
        System.out.println("Factors of 6: ${getFactors(6)}")
        System.out.println("First 20 prime numbers: ${getPrimeNumbers(20)}\n")
        System.out.println("Prime factorization of 20: ${getPrimeFactorization(20, debug=true)}\n\n")
        System.out.println("Prime factorization of 100: ${getPrimeFactorization(100, debug=true)}\n\n")
        System.out.println("Prime factorization of 11500: ${getPrimeFactorization(11500, debug=true)}\n\n")
    }
}