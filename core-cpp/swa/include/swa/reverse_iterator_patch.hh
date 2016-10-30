//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#if __GNUC__ < 4

#ifndef SWA_reverse_iterator_patch_HH
#define SWA_reverse_iterator_patch_HH

// For some reason the gnu libraries seem not to allow a 
// reverse_iterator to be compared to a 
// const_reverse_iterator, whereas it does allow comparison of 
// iterator to a const_iterator. There is no logic I can think 
// of for this, and these functions get round the problem 
// rather neatly. A comment at the bottom of 
// bits/stl_iterator.h in the gnu includes implies that they 
// have had a similar problem with normal iterators, which has 
// been fixed. Maybe they forgot to do the same for 
// reverse_iterators. 

// Further note... looks like I'm right... fixed in GCC v 4.1.2

  template<typename _IteratorL, typename _IteratorR>
    inline bool 
    operator==(const std::reverse_iterator<_IteratorL>& __x, 
	       const std::reverse_iterator<_IteratorR>& __y) 
    { return __x.base() == __y.base(); }

  template<typename _IteratorL, typename _IteratorR>
    inline bool 
    operator<(const std::reverse_iterator<_IteratorL>& __x, 
	       const std::reverse_iterator<_IteratorR>& __y) 
    { return __x.base() < __y.base(); }

  template<typename _IteratorL, typename _IteratorR>
    inline bool 
    operator!=(const std::reverse_iterator<_IteratorL>& __x, 
	       const std::reverse_iterator<_IteratorR>& __y) 
    { return !(__x == __y); }

  template<typename _IteratorL, typename _IteratorR>
    inline bool 
    operator>(const std::reverse_iterator<_IteratorL>& __x, 
	      const std::reverse_iterator<_IteratorR>& __y) 
    { return __y < __x; }

  template<typename _IteratorL, typename _IteratorR>
    inline bool 
    operator<=(const std::reverse_iterator<_IteratorL>& __x, 
		const std::reverse_iterator<_IteratorR>& __y) 
    { return !(__y < __x); }

  template<typename _IteratorL, typename _IteratorR>
    inline bool 
    operator>=(const std::reverse_iterator<_IteratorL>& __x, 
	       const std::reverse_iterator<_IteratorR>& __y) 
    { return !(__x < __y); }

  template<typename _IteratorL, typename _IteratorR>
    inline typename std::reverse_iterator<_IteratorR>::difference_type
    operator-(const std::reverse_iterator<_IteratorL>& __x, 
	      const std::reverse_iterator<_IteratorR>& __y) 
    { return __y.base() - __x.base(); }

#endif

#endif
